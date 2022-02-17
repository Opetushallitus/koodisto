import analyysi from "./koodistoanalyysi.js";
import test from "./koodistotest.js";

if (process.argv[2] === "create") analyysi();
else test();
