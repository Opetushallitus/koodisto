const express = require('express');
const morganBody = require('morgan-body');
const bodyParser = require('body-parser');
const apiMocker = require('connect-api-mocker');
const xmlparser = require('express-xml-bodyparser');

const port = 9000;
const app = express();
const cors = require('cors');
const debug = false;
if (debug) {
    app.use(xmlparser());
    app.use(bodyParser.json());
    morganBody(app);
}
app.use(cors());
app.use('/kayttooikeus-service', apiMocker('src/api/kayttooikeus-service'));
app.use('/organisaatio-service', apiMocker('src/api/organisaatio-service'));
app.use('/lokalisointi', apiMocker('src/api/lokalisointi'));
console.info(`Mock API Server is up and running at: http://localhost:${port}`);
app.listen(port);
