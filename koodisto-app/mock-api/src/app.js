const express = require('express');
const morganBody = require('morgan-body');
const bodyParser = require('body-parser');
const apiMocker = require('connect-api-mocker');
const xmlparser = require('express-xml-bodyparser');
const { createProxyMiddleware } = require('http-proxy-middleware');

const port = 9000;
const app = express();
const cors = require('cors');
const debug = false;
if (debug) {
    app.use(xmlparser());
    app.use(bodyParser.json());
    morganBody(app);
}
const koodistoApp = createProxyMiddleware('http://localhost:8080/koodisto-app');
const koodistoService = createProxyMiddleware('http://localhost:8081/koodisto-service');
app.use(cors());
app.use('/kayttooikeus-service', apiMocker('src/api/kayttooikeus-service'));
app.use('/koodisto-app', koodistoApp);
app.use('/koodisto-service', koodistoService);
console.log(`Mock API Server is up and running at: http://localhost:${port}`);
app.listen(port);
