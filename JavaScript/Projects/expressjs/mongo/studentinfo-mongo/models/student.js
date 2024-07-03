const mongoose = require('mongoose');
const Schema = mongoose.Schema;

mongoose.connect('mongodb://127.0.0.1/student');

mongoose.connection.on('error', (e)=>{
  console.log(e);
})

mongoose.connection.on('open', (e)=>{
  console.log("connected to mongodb");
})

const GeoSchema = new Schema({
  type: {
    type: String,
    default: "Point"
  },
  coordinates: {
    type: [Number],
    index: "2dsphere"
  }
})

const StudentSchema = new Schema({
  id: {
    type: Number,
    min: 1,
    max: 100,
  },
  name: {
    type: String,
    required: [true, 'Name field is required']
  },
  cgpa: {
    // type: mongoose.Decimal128,
    type: Number,
    default: 0.0
  },
  geometry: GeoSchema
});

const Student = mongoose.model('student', StudentSchema);
module.exports = Student;
