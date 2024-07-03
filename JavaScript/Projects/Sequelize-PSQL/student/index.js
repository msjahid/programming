const express = require('express');
const bodyParser = require('body-parser');

//set up express app
const app = express();

app.use(bodyParser.json());

//initialize route
app.use('/api', require('./routes/api'));

//error handling middleware
app.use((err, req, res, next) => {
  res.status(422).send({error: err._message});
  // console.log(err);
})

app.listen(process.env.PORT || 3000, () => {
  console.log(`Server is running on port: ${process.env.PORT || 3000}`);
})
