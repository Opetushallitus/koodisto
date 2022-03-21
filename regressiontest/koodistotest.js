import fs from "fs";
import fetch from "node-fetch";

const correction = (input) => {
    return input
        .replace(/hahtuvaopintopolku/g, "untuvaopintopolku")
        .replace(/json\/.*?\/koodi/g,'codeelement')
        .replace(/rest\/json/g,'rest/codes');
}
export default function () {
    const dir = "./koodistoregression";
    const baseUrl = "http://localhost:8080";

    fs.readFile(dir + "/endpoints.json", "utf-8", (err, data) => {
        if (err) {
            throw err;
        }
        const endpoints = JSON.parse(data.toString());
        endpoints.slice(1, process.argv[3] || 100).forEach((e) => {
            fetch(baseUrl + e.url).then(function (response) {
                response.text().then(function (after) {
                    fs.readFile(e.filename, "utf-8", (error, original) => {
                        if (correction(after) !== original) {
                            console.log('\x1b[31m%s\x1b[0m', e.url, 'fuuuUUuu!!!')
                            fs.writeFile("./error-original.json",
                                original
                                , (err2) => {
                                    if (err2) {
                                        console.error(err2);

                                    }
                                });
                            fs.writeFile("./error-after.json", correction(after)
                                , (err2) => {
                                    if (err2) {
                                        console.error(err2);

                                    }
                                });

                        } else console.log('\x1b[32m%s\x1b[0m', e.url, "ok!");
                    });
                });
            });
        });
    });
    console.log(process.argv);
}
