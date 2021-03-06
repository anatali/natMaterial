// -------------------------------------------------------------------------------------------------
// Copyright (c) Disi-Unibo. All rights reserved.
// Licensed under the MIT License (MIT). See LICENSE in the repo root for license information.
// -------------------------------------------------------------------------------------------------

var path = require('path');

module.exports.TEMPLATE_FILES_LOCATION = path.join(__dirname, '../../DisiFhirConverter/templates');  
module.exports.HL7_DATAFILES_LOCATION  = path.join(__dirname, '../../DisiFhirConverter/sampleData');  

//For CLS
module.exports.CLS_NAMESPACE			  = 'conversionRequest';
module.exports.CLS_KEY_HANDLEBAR_INSTANCE = 'hbs';
module.exports.CLS_KEY_TEMPLATE_LOCATION  = 'templateLocation';