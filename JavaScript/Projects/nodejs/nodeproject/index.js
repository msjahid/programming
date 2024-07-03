const express = require('express');
const app = express();
const bodyParser = require('body-parser');
const routes = require ('./routes');

app.use(bodyParser.json());
routes(app);

app.listen(4000);
