import fs from "fs";
import fetch from "node-fetch";

export default function () {
    const dir = "./koodistoregression";
    const baseUrl = "https://virkailija.hahtuvaopintopolku.fi";

  fs.readFile(dir + "/endpoints.json", "utf-8", (err, data) => {
    if (err) {
      throw err;
    }
    const endpoints = JSON.parse(data.toString());
    endpoints.slice(1,2).forEach((e) => {
      fetch(baseUrl + e.url).then(function (response) {
        response.text().then(function (after) {
          fs.readFile(e.filename, "utf-8", (error, original) => {
            if (after !== original) console.log({after, original});
            //else console.log(e.url + "ok!");
          });
        });
      });
    });
  });
}
