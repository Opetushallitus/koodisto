import fs from "fs";
import fetch from "node-fetch";
export default function () {
  const dir = "./koodistoregression";
  if (!fs.existsSync(dir)) {
    fs.mkdirSync(dir);
  }
  fs.readFile("koodistoanalyysi.log", "utf8", function (err, data) {
    if (err) throw err;
    const rows = data.split("\n");
    console.log("API", rows[0]);
    const x = rows
      .filter((a) => !!a)
      .map((a) => a.substring(a.indexOf("{"), a.indexOf("}") + 1))
      .map((a, i) => {
        try {
          return JSON.parse(a);
        } catch (e) {
          return { e };
        }
      })
      .filter((a) => a.requestMethod === "GET")
      .filter((a) => a.responseCode === "200")
      .filter((a) => !a.request.includes("koodisto-service/rest/api-docs"))
      .filter((a) => !a.request.includes("koodisto-service/rest/swagger"))
      .filter(
        (a) =>
          !a.request.includes(
            "koodisto-service/rest/session/maxinactiveinterval"
          )
      )
      .filter((a) => !a.request.includes("GET /koodisto-ui/"))
      .filter(
        (a) =>
          a["caller-id"] !==
          "1.2.246.562.10.00000000001.koodisto.koodisto-ui.frontend"
      )
      .filter((a) => !a.request.includes("j_spring_cas"))
      .map((a) => a.request)
      .map((a) => a.substring(4, a.indexOf("HTTP/1.1")).trim());
    const rest = [];
    const foo = new Set();
    const endpoints = new Map();
    const xml = new Map();
    const requestMap = new Map();
    x.forEach((a) => {
      if (a === "/koodisto-service/buildversion.txt")
        endpoints.set(a, (endpoints.get(a) || 0) + 1);
      else if (
        a.match(/\/koodisto-service\/rest\/codes\/[a-z0-9]*(\?noCache=\d*)?$/)
      ) {
        endpoints.set(
          "/koodisto-service/rest/codes/arviointiasteikkolisapisteetib?noCache=1613032017829",
          (endpoints.get(
            "/koodisto-service/rest/codes/arviointiasteikkolisapisteetib?noCache=1613032017829"
          ) || 0) + 1
        );
      } else if (
        a.match(
          /\/koodisto-service\/rest\/codes\/[a-z0-9]*\/\d*(\?noCache=\d*)?$$/
        )
      ) {
        endpoints.set(
          "/koodisto-service/rest/codes/koskiopiskeluoikeudentila/1?noCache=1613037333144",
          (endpoints.get(
            "/koodisto-service/rest/codes/koskiopiskeluoikeudentila/1?noCache=1613037333144"
          ) || 0) + 1
        );
      } else if (
        a.match(
          /\/koodisto-service\/rest\/codeelement\/codes\/[a-z0-9\-_]*\/\d*(\?noCache=\d*)?$/
        )
      ) {
        endpoints.set(
          "/koodisto-service/rest/codeelement/codes/suorituksentila/1",
          (endpoints.get(
            "/koodisto-service/rest/codeelement/codes/suorituksentila/1"
          ) || 0) + 1
        );
      } else if (
        a.match(
          /\/koodisto-service\/rest\/codeelement\/latest\/[a-z0-9\-_]*(\?noCache=\d*)?$/
        )
      ) {
        endpoints.set(
          "/koodisto-service/rest/codeelement/latest/koulutus_354245",
          (endpoints.get(
            "/koodisto-service/rest/codeelement/latest/koulutus_354245"
          ) || 0) + 1
        );
      } else if (
        a.match(
          /\/koodisto-service\/rest\/json\/searchKoodis\?koodiUris=([A-Za-z0-9_\-]*)&koodiVersio=\d*&koodiVersioSelection=SPECIFIC$/
        )
      ) {
        endpoints.set(
          "/koodisto-service/rest/json/searchKoodis?koodiUris=koulutusohjelmaamm_0007&koodiVersio=1&koodiVersioSelection=SPECIFIC",
          (endpoints.get(
            "/koodisto-service/rest/json/searchKoodis?koodiUris=koulutusohjelmaamm_0007&koodiVersio=1&koodiVersioSelection=SPECIFIC"
          ) || 0) + 1
        );
      } else if (
        a.match(
          /\/koodisto-service\/rest\/json\/searchKoodis\?koodiUris=([A-Za-z0-9_\-]*)&koodiVersioSelection=LATEST$/
        )
      ) {
        endpoints.set(
          "/koodisto-service/rest/json/searchKoodis?koodiUris=posti_00520&koodiVersioSelection=LATEST",
          (endpoints.get(
            "/koodisto-service/rest/json/searchKoodis?koodiUris=posti_00520&koodiVersioSelection=LATEST"
          ) || 0) + 1
        );
      } else if (
        a.match(
          /\/koodisto-service\/rest\/json\/searchKoodis\?(koodiUris=([A-Za-z0-9_\-]*))(&koodiUris=([A-Za-z0-9_\-]*))*(&noCache=\d*)?$/
        )
      ) {
        endpoints.set(
          "/koodisto-service/rest/json/searchKoodis?koodiUris=kieli_fi&koodiUris=kieli_sv&koodiUris=kieli_en&koodiUris=kunta_734&koodiUris=maatjavaltiot1_fin&koodiUris=oppilaitoksenopetuskieli_1&koodiUris=posti_24800&noCache=1613031593074",
          (endpoints.get(
            "/koodisto-service/rest/json/searchKoodis?koodiUris=kieli_fi&koodiUris=kieli_sv&koodiUris=kieli_en&koodiUris=kunta_734&koodiUris=maatjavaltiot1_fin&koodiUris=oppilaitoksenopetuskieli_1&koodiUris=posti_24800&noCache=1613031593074"
          ) || 0) + 1
        );
      } else if (
        a.match(
          /\/koodisto-service\/rest\/json\/searchKoodis\?koodiUris=&koodiVersioSelection=LATEST/
        )
      ) {
        endpoints.set(
          "/koodisto-service/rest/json/searchKoodis?koodiUris=&koodiVersioSelection=LATEST",
          (endpoints.get(
            "/koodisto-service/rest/json/searchKoodis?koodiUris=&koodiVersioSelection=LATEST"
          ) || 0) + 1
        );
      } else if (
        a.match(
          /\/koodisto-service\/rest\/codeelement\/[a-z0-9-\_]*(\d*|[a-z]*)\/\d*(\?noCache=\d*)?$/
        )
      ) {
        endpoints.set(
          "/koodisto-service/rest/codeelement/opetuspisteet_0177301/1",
          (endpoints.get(
            "/koodisto-service/rest/codeelement/opetuspisteet_0177301/1"
          ) || 0) + 1
        );
      } else if (
        a.match(
          /\/koodisto-service\/rest\/json\/relaatio\/sisaltyy-ylakoodit\/[a-z_\-0-9]*(\?koodiVersio=\d*)?$/
        )
      ) {
        endpoints.set(
          "/koodisto-service/rest/json/relaatio/sisaltyy-ylakoodit/aikuhakukohteet_4106?koodiVersio=4",
          (endpoints.get(
            "/koodisto-service/rest/json/relaatio/sisaltyy-ylakoodit/aikuhakukohteet_4106?koodiVersio=4"
          ) || 0) + 1
        );
      } else if (
        a.match(
          /\/koodisto-service\/rest\/json\/relaatio\/rinnasteinen\/[a-z0-9_]*(\d*|[a-z]*)\?koodiVersio=\d*$/
        )
      ) {
        endpoints.set(
          "/koodisto-service/rest/json/relaatio/rinnasteinen/maatjavaltiot2_744?koodiVersio=1",
          (endpoints.get(
            "/koodisto-service/rest/json/relaatio/rinnasteinen/maatjavaltiot2_744?koodiVersio=1"
          ) || 0) + 1
        );
      } else if (
        a.match(
          /\/koodisto-service\/rest\/json\/relaatio\/rinnasteinen\/[a-z0-9_]*(\d*|[a-z]*)\??$/
        )
      ) {
        endpoints.set(
          "/koodisto-service/rest/json/relaatio/rinnasteinen/maatjavaltiot1_ukr",
          (endpoints.get(
            "/koodisto-service/rest/json/relaatio/rinnasteinen/maatjavaltiot1_ukr"
          ) || 0) + 1
        );
      } else if (
        a.match(
          /\/koodisto-service\/rest\/json\/relaatio\/sisaltyy-alakoodit\/[a-z,\_]*(\d*|[a-z]*)\?koodiVersio=\d*$/
        )
      ) {
        endpoints.set(
          "/koodisto-service/rest/json/relaatio/sisaltyy-alakoodit/koulutus_000001?koodiVersio=12",
          (endpoints.get(
            "/koodisto-service/rest/json/relaatio/sisaltyy-alakoodit/koulutus_000001?koodiVersio=12"
          ) || 0) + 1
        );
      } else if (
        a.match(
          /\/koodisto-service\/rest\/json\/relaatio\/sisaltyy-ylakoodit\/[a-z,\_]*(\d*|[a-z]*)$/
        )
      ) {
        endpoints.set(
          "/koodisto-service/rest/json/relaatio/sisaltyy-ylakoodit/aikuhakukohteet_4106",
          (endpoints.get(
            "/koodisto-service/rest/json/relaatio/sisaltyy-ylakoodit/aikuhakukohteet_4106"
          ) || 0) + 1
        );
      } else if (
        a.match(
          /\/koodisto-service\/rest\/json\/relaatio\/sisaltyy-alakoodit\/[a-z,\_]*(\d*|[a-z]*)$/
        )
      ) {
        endpoints.set(
          "/koodisto-service/rest/json/relaatio/sisaltyy-alakoodit/koulutus_000001",
          (endpoints.get(
            "/koodisto-service/rest/json/relaatio/sisaltyy-alakoodit/koulutus_000001"
          ) || 0) + 1
        );
      } else if (
        a.match(
          /\/koodisto-service\/rest\/json\/relaatio\/sisaltyy-alakoodit\/[a-z,\_]*(\d*|[a-z]*)/
        )
      ) {
        endpoints.set(
          "/koodisto-service/rest/json/relaatio/sisaltyy-alakoodit/julkaisunpaaluokka_i",
          (endpoints.get(
            "/koodisto-service/rest/json/relaatio/sisaltyy-alakoodit/julkaisunpaaluokka_i"
          ) || 0) + 1
        );
      } else if (
        a.match(/\/koodisto-service\/rest\/json\/[a-z,0-9]*\/koodi\??$/)
      ) {
        endpoints.set(
          "/koodisto-service/rest/json/hakukohteet/koodi",
          (endpoints.get("/koodisto-service/rest/json/hakukohteet/koodi") ||
            0) + 1
        );
      } else if (
        a.match(
          /\/koodisto-service\/rest\/json\/[a-z,0-9]*\/koodi\?(onlyValidKoodis=true&)?noCache=\d*(&onlyValidKoodis=true)?$/
        )
      ) {
        endpoints.set(
          "/koodisto-service/rest/json/organisaatiotyyppi/koodi?onlyValidKoodis=true&noCache=1613031568957",
          (endpoints.get(
            "/koodisto-service/rest/json/organisaatiotyyppi/koodi?onlyValidKoodis=true&noCache=1613031568957"
          ) || 0) + 1
        );
      } else if (
        a.match(
          /\/koodisto-service\/rest\/json\/[a-z,0-9]*\/koodi\?onlyValidKoodis=true(&koodistoVersio=)?$/
        )
      ) {
        endpoints.set(
          "/koodisto-service/rest/json/valintatapajono/koodi?onlyValidKoodis=true&koodistoVersio=",
          (endpoints.get(
            "/koodisto-service/rest/json/valintatapajono/koodi?onlyValidKoodis=true&koodistoVersio="
          ) || 0) + 1
        );
      } else if (
        a.match(
          /\/koodisto-service\/rest\/json\/[a-z,0-9]*\/koodi\?onlyValidKoodis=true(&koodistoVersio=\d*)?$/
        )
      ) {
        endpoints.set(
          "/koodisto-service/rest/json/oppilaitoksenopetuskieli/koodi?onlyValidKoodis=true&koodistoVersio=1",
          (endpoints.get(
            "/koodisto-service/rest/json/oppilaitoksenopetuskieli/koodi?onlyValidKoodis=true&koodistoVersio=1"
          ) || 0) + 1
        );
      } else if (
        a.match(
          /\/koodisto-service\/rest\/json\/[a-z,0-9]*\/koodi(\?onlyValidKoodis=false)?\??$/
        )
      ) {
        endpoints.set(
          "/koodisto-service/rest/json/hakukohteet/koodi?onlyValidKoodis=false",
          (endpoints.get(
            "/koodisto-service/rest/json/hakukohteet/koodi?onlyValidKoodis=false"
          ) || 0) + 1
        );
      } else if (
        a.match(
          /\/koodisto-service\/rest\/json\/[a-z,0-9]*\/koodi\?onlyValidKoodis=false&koodistoVersio=\d*/
        )
      ) {
        endpoints.set(
          "/koodisto-service/rest/json/hyvaksynnanehdot/koodi?onlyValidKoodis=false&koodistoVersio=1",
          (endpoints.get(
            "/koodisto-service/rest/json/hyvaksynnanehdot/koodi?onlyValidKoodis=false&koodistoVersio=1"
          ) || 0) + 1
        );
      } else if (
        a.match(
          /\/koodisto-service\/rest\/json\/[a-z_0-9]*\/koodi\?koodistoVersio=\d*&?$/
        )
      ) {
        endpoints.set(
          "/koodisto-service/rest/json/hyvaksynnanehdot/koodi?koodistoVersio=1",
          (endpoints.get(
            "/koodisto-service/rest/json/hyvaksynnanehdot/koodi?koodistoVersio=1"
          ) || 0) + 1
        );
      } else if (
        a.match(
          /\/koodisto-service\/rest\/json\/[a-z,0-9]*\/koodi\/[a-z0-9_\-]*\??$/
        )
      ) {
        endpoints.set(
          "/koodisto-service/rest/json/oppilaitosnumero/koodi/oppilaitosnumero_02472",
          (endpoints.get(
            "/koodisto-service/rest/json/oppilaitosnumero/koodi/oppilaitosnumero_02472"
          ) || 0) + 1
        );
      } else if (
        a.match(
          /\/koodisto-service\/rest\/json\/[a-z0-9]*\/koodi\/[a-z0-9_]*\?koodistoVersio=\d*&?$/
        )
      ) {
        endpoints.set(
          "/koodisto-service/rest/json/opetustehtava/koodi/opetustehtava_10?koodistoVersio=1",
          (endpoints.get(
            "/koodisto-service/rest/json/opetustehtava/koodi/opetustehtava_10?koodistoVersio=1"
          ) || 0) + 1
        );
      } else if (
        a.match(/\/koodisto-service\/rest\/json\/[a-z,0-9]*(\?noCache=\d*)?$/)
      ) {
        endpoints.set(
          "/koodisto-service/rest/json/kunta",
          (endpoints.get("/koodisto-service/rest/json/kunta") || 0) + 1
        );
      } else if (
        a.match(
          /\/koodisto-service\/rest\/json\/[a-z,0-9]*\/koodi\/arvo\/[a-zA-Z.0-9]*(\?noCache=\d*)?$/
        )
      ) {
        endpoints.set(
          "/koodisto-service/rest/json/kielikoodistoopetushallinto/koodi/arvo/FI",
          (endpoints.get(
            "/koodisto-service/rest/json/kielikoodistoopetushallinto/koodi/arvo/FI"
          ) || 0) + 1
        );
      } else if (a.match(/\/koodisto-service\/rest\/[a-z,0-9]*\/koodi$/)) {
        xml.set(
          "/koodisto-service/rest/virtaopiskeluoikeudentila/koodi",
          (xml.get("/koodisto-service/rest/virtaopiskeluoikeudentila/koodi") ||
            0) + 1
        );
      } else if (
        a.match(
          /\/koodisto-service\/rest\/json\/searchKoodis\?koodiUris=[A-Za-z0-9%\-.()!]*&koodiVersioSelection=LATEST$/
        )
      ) {
        foo.add(a);
      } else {
        rest.push(a);
      }
      requestMap.set("count", (requestMap.get("count") || 0) + 1);
    });
    console.log(requestMap);
    console.log("foo", foo);
    console.log("tobetested" + "", endpoints);
    console.log("tobetested xml" + "", xml);
    console.log(rest.length, rest[0]);
    const baseUrl = "http://localhost:8081";
    const toTest = [];
    for (const entry of endpoints.entries()) {
      const url = entry[0]
        .replace(/=/g, "")
        .replace(/\//g, "")
        .replace(/_/g, "")
        .replace(/&/g, "")
        .replace(/-/g, "")
        .replace(/\?/, "");
      const filename = dir + "/" + url + ".json";
      toTest.push({ filename, url: entry[0] });
      fetch(baseUrl + entry[0]).then(function (response) {
        response.text().then(function (text) {
          fs.writeFile(filename, text, (err) => {
            if (err) {
              console.error(err);
              return;
            }
          });
        });
      });
    }
    fs.writeFile(dir + "/endpoints.json", JSON.stringify(toTest), (err) => {
      if (err) {
        console.error(err);
        return;
      }
    });
  });
}
