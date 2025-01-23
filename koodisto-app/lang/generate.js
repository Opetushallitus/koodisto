'use strict';

const fs = require('fs');

const rawdata = fs.readFileSync('lang/default-translations.json');
const translations = JSON.parse(rawdata);
const oid = '1.2.246.562.24.39768521067';
const data = Object.keys(translations)
    .map((key) => {
        const trans = translations[key];
        return (
            `insert into localisation (id, version, accessed, created, modified, createdby, modifiedby, xcategory, xlanguage, xkey, xvalue) ` +
            `values(nextval('hibernate_sequence'),0,now(),now(),now(),'${oid}','${oid}','koodisto','sv','${key}','${trans.defaultMessage}');\n` +
            `insert into localisation (id, version, accessed, created, modified, createdby, modifiedby, xcategory, xlanguage, xkey, xvalue) ` +
            `values(nextval('hibernate_sequence'),0,now(),now(),now(),'${oid}','${oid}','koodisto','en','${key}','${trans.defaultMessage}');\n` +
            `insert into localisation (id, version, accessed, created, modified, createdby, modifiedby, xcategory, xlanguage, xkey, xvalue) ` +
            `values(nextval('hibernate_sequence'),0,now(),now(),now(),'${oid}','${oid}','koodisto','fi','${key}','${trans.defaultMessage}')` +
            `on conflict on constraint localisation_xcategory_xlanguage_xkey_key do update set xvalue = excluded.xvalue;`
        );
    })
    .join('\n');
console.log(data);
fs.writeFileSync('lang/default-translations.sql', data);
