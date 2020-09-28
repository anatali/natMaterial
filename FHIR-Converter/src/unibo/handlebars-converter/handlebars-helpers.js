// -------------------------------------------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License (MIT). See LICENSE in the repo root for license information.
// -------------------------------------------------------------------------------------------------

var HandlebarsUtils = require('handlebars').Utils;
var constants       = require('../constants/constants');
var fs              = require('fs');
/*
var uuidv3 = require('uuid/v3'); 
import { v3 as uuidv3 } from 'uuid';
var crypto          = require('crypto');
var jsonProcessor   = require('../outputProcessor/jsonProcessor');
var specialCharProcessor = require('../inputProcessor/specialCharProcessor');
var zlib = require('zlib');
*/

// Some helpers will be referenced in other helpers and declared outside the export below.
var getSegmentListsInternal = function (msg, ...segmentIds) {
    var ret = {};
    for (var s = 0; s < segmentIds.length - 1; s++) { //-1 because segmentsIds includes the full message at the end
        var segOut = [];
        for (var i = 0; i < msg.meta.length; i++) {
            if (msg.meta[i] == segmentIds[s]) {
                segOut.push(msg.data[i]);
            }
        }
        ret[segmentIds[s]] = segOut;
    }
    return ret;
};

var normalizeSectionName = function (name) {
    return name.replace(/[^A-Za-z0-9]/g, '_');
};

// check the date is valid
var validDate = function (year, monthIndex, day){
    var dateInstance = new Date(year, monthIndex, day);
    if(dateInstance.getFullYear() === Number(year) && dateInstance.getMonth() === Number(monthIndex) && dateInstance.getDate() === Number(day))
        return true;
    return false;
};

// check the datetime is valid
var validUTCDateTime = function (dateTimeComposition){
    for (var key in dateTimeComposition)
        dateTimeComposition[key] = Number(dateTimeComposition[key]);
    var dateInstance = new Date(Date.UTC(dateTimeComposition.year, dateTimeComposition.month - 1, dateTimeComposition.day, dateTimeComposition.hours, dateTimeComposition.minutes, dateTimeComposition.seconds, dateTimeComposition.milliseconds));
    if(dateInstance.getUTCFullYear() === dateTimeComposition.year && dateInstance.getUTCMonth() === dateTimeComposition.month - 1 
        && dateInstance.getUTCDate() === dateTimeComposition.day && dateInstance.getUTCHours() === dateTimeComposition.hours 
        && dateInstance.getUTCMinutes() === dateTimeComposition.minutes && dateInstance.getSeconds() === dateTimeComposition.seconds 
        && dateInstance.getMilliseconds() === dateTimeComposition.milliseconds)
        return true;
    return false;
};

// check the string is valid
var validDatetimeString = function (dateTimeString) {
    if (!dateTimeString || dateTimeString.toString() === '')
        return false;
    // datetime format in the spec: YYYY[MM[DD[HH[MM[SS[.S[S[S[S]]]]]]]]][+/-ZZZZ],
    var ds = dateTimeString.toString();
    if (!/^(\d{4}(\d{2}(\d{2}(\d{2}(\d{2}(\d{2}(\.\d+)?)?)?)?)?)?((-|\+)\d{1,4})?)$/.test(ds))
        throw `Bad input for Datetime type in ${ds}`;
    return true;
};

// convert the dateString to date string with hyphens
var convertDate = function (dateString) {
    if (dateString.length === 4)
        return dateString;
    if (dateString.length === 6 || dateString.length >= 8){
        var year = dateString.substring(0, 4);
        var month = dateString.substring(4, 6);
        if (month <= 0 || month > 12)
            throw `Invalid month: ${dateString}`;
        if (dateString.length === 6)
            return year + '-' + month;
        var day = dateString.substring(6, 8);
        if (!validDate(year, month - 1, day))
            throw `Invalid day: ${dateString}`;
        return year + '-' + month + '-' + day;
    }
    throw `Bad input for Date type in ${dateString}`;
};


// handling the date format here
var getDate = function (dateString) {
    if (!validDatetimeString(dateString))
        return '';
    return convertDate(dateString.toString());
};

var getDateTimeComposition = function (ds){
    ds = ds.replace('.','');
    ds = ds.padEnd(17, '0');
    var year = ds.substring(0,4);
    var month = ds.substring(4,6);
    var day = ds.substring(6,8);
    var hours = ds.substring(8,10);
    var minutes = ds.substring(10,12);
    var seconds = ds.substring(12,14);
    var milliseconds = ds.substring(14, 17);
    var dateTimeComposition = {
        'year': year,
        'month': month,
        'day': day,
        'hours': hours,
        'minutes': minutes,
        'seconds': seconds,
        'milliseconds': milliseconds
    };
    return dateTimeComposition;
};

// handling the datetime format here
var getDateTime = function (dateTimeString) {
    if (!validDatetimeString(dateTimeString))
        return '';

    // handle the datetime format with time zone
    var ds = dateTimeString.toString();
    var timeZoneChar = '';
    if (ds.indexOf('-') !== -1)
        timeZoneChar = '-';
    else if (ds.indexOf('+') !== -1)
        timeZoneChar = '+'; 
    if (timeZoneChar !== '')
    {
        var dateSections = ds.split(timeZoneChar);
        var dateTimeComposition = getDateTimeComposition(dateSections[0]);
        var date = dateTimeComposition.year + '-' + dateTimeComposition.month + '-' + dateTimeComposition.day;
        var time = dateTimeComposition.hours + ':' + dateTimeComposition.minutes + ':' + dateTimeComposition.seconds + ':' + dateTimeComposition.milliseconds;
        var timezone = timeZoneChar + dateSections[1];
        if (!validUTCDateTime(dateTimeComposition))
            throw `Invalid datetime: ${ds}`;
        return new Date(date + ' ' + time + ' ' + timezone).toISOString();
    }

    if (ds.length <= 8)
        return convertDate(ds);
    
    // Padding 0s to 17 digits 
    dateTimeComposition = getDateTimeComposition(ds);
    if (!validUTCDateTime(dateTimeComposition))
        throw `Invalid datetime: ${ds}`;
    return (new Date(Date.UTC(dateTimeComposition.year, dateTimeComposition.month - 1, dateTimeComposition.day, dateTimeComposition.hours, dateTimeComposition.minutes, dateTimeComposition.seconds, dateTimeComposition.milliseconds))).toJSON();
};

module.exports.internal = {
    getDateTime: getDateTime,
    getDate: getDate
};

module.exports.external = [

    {
        name: 'getFirstSegments',
        description: "Returns first instance of the segments e.g. getFirstSegments msg.v2 'PID' 'PD1': getFirstSegments message segment1 segment2 …",
        func: function getFirstSegments(msg, ...segmentIds) {
            try {
                var ret = {};
                var inSegments = {};
                for (var s = 0; s < segmentIds.length - 1; s++) { //-1 because segmentsIds includes the full message at the end
                    inSegments[segmentIds[s]] = true;
                }
                for (var i = 0; i < msg.meta.length; i++) {
                    if (inSegments[msg.meta[i]] && !ret[msg.meta[i]]) {
                        ret[msg.meta[i]] = msg.data[i];
                    }
                }
                return ret;
            }
            catch (err) {
                throw `helper "getFirstSegments" : ${err}`;
            }
        }
    },
    {
        name: 'getSegmentLists',
        description: 'Extract HL7 v2 segments: getSegmentLists message segment1 segment2 …',
        func: function getSegmentLists(msg, ...segmentIds) {
            try {
                return getSegmentListsInternal(msg, ...segmentIds);
            }
            catch (err) {
                throw `helper "getSegmentLists" : ${err}`;
            }
        }
    },
    {
        name: 'getRelatedSegmentList',
        description: 'Given a segment name and index, return the collection of related named segments: getRelatedSegmentList message parentSegmentName parentSegmentIndex childSegmentName',
        func: function getRelatedSegmentList(msg, parentSegment, parentIndex, childSegment) {
            try {
                var ret = {};
                var segOut = [];
                var parentFound = false;
                var childIndex = -1;

                for (var i = 0; i < msg.meta.length; i++) {
                    if (msg.meta[i] == parentSegment && msg.data[i][0] == parentIndex) {
                        parentFound = true;
                    }
                    else if (msg.meta[i] == childSegment && parentFound == true) {
                        childIndex = i;
                        break;
                    }
                }

                if (childIndex > -1) {
                    do {
                        segOut.push(msg.data[childIndex]);
                        childIndex++;
                    } while (childIndex < msg.meta.length && msg.meta[childIndex] == childSegment);
                }

                ret[childSegment] = segOut;
                return ret;
            }
            catch (err) {
                throw `helper "getRelatedSegmentList" : ${err}`;
            }
        }

    },
    {
        name: 'getParentSegment',
        description: 'Given a child segment name and overall message index, return the first matched parent segment: getParentSegment message childSegmentName childSegmentIndex parentSegmentName',
        func: function getParentSegment(msg, childSegment, childIndex, parentSegment) {
            try {
                var ret = {};
                var msgIndex = -1;
                var parentIndex = -1;
                var foundChildSegmentCount = -1;

                for (var i = 0; i < msg.meta.length; i++) {
                    if (msg.meta[i] == childSegment) {
                        // count how many segments of the child type that we have found
                        // as the passed in child index is relative to the entire message
                        foundChildSegmentCount++;
                        if (foundChildSegmentCount == childIndex) {
                            msgIndex = i;
                            break;
                        }
                    }
                }

                // search backwards from the found child for the first instance
                // of the parent segment type
                for (i = msgIndex; i > -1; i--) {
                    if (msg.meta[i] == parentSegment) {
                        parentIndex = i;
                        break;
                    }
                }

                if (parentIndex > -1) {
                    ret[parentSegment] = msg.data[parentIndex];
                }

                return ret;
            }
            catch (err) {
                throw `helper "getParentSegment" : ${err}`;
            }
        }
    },
    {
        name: 'hasSegments',
        description: 'Check if HL7 v2 message has segments: hasSegments message segment1 segment2 …',
        func: function (msg, ...segmentIds) {
            try {
                var exSeg = getSegmentListsInternal(msg, ...segmentIds);
                for (var s = 0; s < segmentIds.length - 1; s++) { //-1 because segmentsIds includes the full message at the end
                    if (!exSeg[segmentIds[s]] || exSeg[segmentIds[s]].length == 0) {
                        return false;
                    }
                }
                return true;
            }
            catch (err) {
                throw `helper "hasSegments" : ${err}`;
            }
        }
    },
    {
        name: 'concat',
        description: 'Returns the concatenation of provided strings: concat aString bString cString …',
        func: function (...values) {
            if (Array.isArray(values[0])) {
                return [].concat(...(values.slice(0, -1))); //last element is full msg
            }
            return ''.concat(...(values.slice(0, -1))); //last element is full msg
        }
    },
    {
        name: 'generateUUID',
        description: 'Generates a guid based on a URL: generateUUID url',
        func: function (urlNamespace) {
            return uuidv3(''.concat(urlNamespace), uuidv3.URL);
        }
    },
    {
        name: 'addHyphensSSN',
        description: 'Adds hyphens to a SSN without hyphens: addHyphensSSN SSN',
        func: function (ssn) {
            try {
                ssn = ssn.toString();

                // Should be 9 digits
                if (!/^\d{9}$/.test(ssn)) {
                    return ssn;
                }

                return ssn.substring(0, 3) + '-' + ssn.substring(3, 5) + '-' + ssn.substring(5, 9);
            }
            catch (err) {
                return '';
            }
        }
    },
    {
        name: 'addHyphensDate',
        description: 'Adds hyphens to a date without hyphens: addHyphensDate date',
        func: function (date) {
            try {
                return getDate(date);
            }
            catch (err) {
                throw `helper "addHyphensDate" : ${err}`;
            }
        }
    },
    {
        name: 'now',
        description: 'Provides current UTC time in YYYYMMDDHHmmss.SSS format: now',
        func: function () {
            var datetimeString = (new Date()).toISOString().replace(/[^0-9]/g, "");
            return datetimeString.slice(0, 14) + '.' + datetimeString.slice(14, 17);
        }
    },
    {
        name: 'formatAsDateTime',
        description: 'Converts an  YYYY[MM[DD[HH[MM[SS[.S[S[S[S]]]]]]]]][+/-ZZZZ] string, e.g. 20040629175400.000 to dateTime format, e.g. 2004-06-29T17:54:00.000z: formatAsDateTime(dateTimeString)',
        func: function (dateTimeString) {
            try {
                return getDateTime(dateTimeString);
            }
            catch (err) {
                throw `helper "formatAsDateTime" : ${err}`;
            }
        }
    },
    {
        name: 'toString',
        description: 'Converts to string: toString object',
        func: function (o) {
            return o.toString();
        }
    },
    {
        name: 'toJsonString',
        description: 'Converts to JSON string: toJsonString object',
        func: function (o) {
            return JSON.stringify(o);
        }
    },
    {
        name: 'toLower',
        description: 'Converts string to lower case: toLower string',
        func: function (o) {
            try {
                return o.toString().toLowerCase();
            }
            catch (err) {
                return '';
            }
        }
    },
    {
        name: 'toUpper',
        description: 'Converts string to upper case: toUpper string',
        func: function (o) {
            try {
                return o.toString().toUpperCase();
            }
            catch (err) {
                return '';
            }
        }
    },
    {
        name: 'isNaN',
        description: 'Checks if the object is not a number using JavaScript isNaN: isNaN object',
        func: function (o) {
            return isNaN(o);
        }
    },
    {
        name: 'abs',
        description: 'Returns the absolute value of a number: abs number',
        func: function (x) {
            return Math.abs(x);
        }
    },
    {
        name: 'ceil',
        description: 'Returns the next largest whole number or integer: ceil number',
        func: function (x) {
            return Math.ceil(x);
        }
    },
    {
        name: 'floor',
        description: 'Returns the largest integer less than or equal to a given number: floor number',
        func: function (x) {
            return Math.floor(x);
        }
    },
    {
        name: 'max',
        description: 'Returns the largest of zero or more numbers: max number1, number2, number3 . . .',
        func: function () {
            var args = [];
            for (var i = 0; i < arguments.length - 1; i++)
                args[i] = arguments[i];
            return Math.max(...args);
        }
    },
    {
        name: 'min',
        description: 'Returns the lowest-valued number passed into it, or NaN if any parameter isn\'t a number and can\'t be converted into one: min number1, number2, number3 . . .',
        func: function () {
            var args = [];
            for (var i = 0; i < arguments.length - 1; i++)
                args[i] = arguments[i];
            return Math.min(...args);
        }
    },
    {
        name: 'pow',
        description: 'Returns the base to the exponent power, that is, base^exponent.: pow base, exponent',
        func: function (x,y) {
            return Math.pow(x,y);
        }
    },
    {
        name: 'random',
        description: 'Returns a floating-point, pseudo-random number in the range 0 to less than 1 (inclusive of 0, but not 1) with approximately uniform distribution over that range — which you can then scale to your desired range: random',
        func: function () {
            return Math.random();
        }
    },
    {
        name: 'round',
        description: 'Returns the value of a number rounded to the nearest integer: round number',
        func: function (x) {
            return Math.round(x);
        }
    },
    {
        name: 'sign',
        description: 'returns either a positive or negative +/- 1, indicating the sign of a number passed into the argument. If the number passed into is 0, it will return a +/- 0. Note that if the number is positive, an explicit (+) will not be returned: sign number',
        func: function (x) {
            return Math.sign(x);
        }
    },
    {
        name: 'trunc',
        description: 'Returns the integer part of a number by removing any fractional digits: trunc number',
        func: function (x) {
            return Math.trunc(x);
        }
    },
    {
        name: 'add',
        description: 'add two numbers: + number1 number 2',
        func: function (x, y) {
            return Number(x) + Number(y);
        }
    },
    {
        name: 'subtract',
        description: 'subtract second number from the first: - number1 number2',
        func: function (x, y) {
            return Number(x) - Number(y);
        }
    },
    {
        name: 'multiply',
        description: 'multiply two numbers: * number1 number2',
        func: function (x, y) {
            return Number(x) * Number(y);
        }
    },
    {
        name: 'divide',
        description: 'divide first number by the second number: / number1 number2',
        func: function (x, y) {
            return Number(x) / Number(y);
        }
    }
];
