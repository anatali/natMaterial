// -------------------------------------------------------------------------------------------------
// Copyright (c) Disi-Unibo. All rights reserved.
// Licensed under the MIT License (MIT). See LICENSE in the repo root for license information.
// -------------------------------------------------------------------------------------------------

let indexerRegex =  /(?<=(?<!\\){{[^}]*?)(-)(\d+)/g; // e.g. replace -2 with ".[1]";
//new RegExp("(?<!\\){{[^}]*?)(-)(\d+)", "g"); //  
module.exports.Process = function (input) {
    return input.replace(indexerRegex, replacer);	//TODO BY AN input; //
};

function replacer(match, p1, p2) {
    if (p2 > 0) {
        return `.[${p2-1}]`;
    }
    else {
        return `${p1}${p2}`;
    }
}

/*

 (?<={index:)\d+(?=})
 /{index:(\d+)}/g

(?<=(?<!\\){{[^}]*?)(-)(\d+)

(?<!\\){{[^}]*?)(-)(\d+)


*/