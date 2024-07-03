const pg = require("pg");

// create the connection to database
const client = new pg.Client({
  host: '127.0.0.1',
  user: 'postgres',
  password: '5271',
  port: '5432',
  database: 'student'
});

client.connect( (err) => {
    if(err){
        console.log(err);
    }else{
        console.log("connected");
    }
});

module.exports = client;
