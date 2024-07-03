const mongoose = require('mongoose');
const { isEmail } = require('validator');
const bcrypt = require('bcrypt');

const Schema = mongoose.Schema;

mongoose.connect('mongodb://127.0.0.1/doctor');

mongoose.connection.on('error', (e)=>{
  console.log(e);
})

mongoose.connection.on('open', (e)=>{
  console.log("connected to mongodb");
})

const userSchema = new Schema({
  email: {
    type: String,
    required: [true, 'Email is required'],
    unique: true,
    lowercase: true,
    validate: [isEmail, 'Enter a valid Email! Try again.']
  },
  password: {
    type: String,
    required: true,
    minlength: [8, 'Password is too short!']
  }
});

//Fire a function after doc saved to db
/*userSchema.post('save', function(doc, next) {
  console.log('New user created successfully', doc);
  next();
});*/

//Fire a function before doc saved to db
userSchema.pre('save', async function  (next) {
  const saltRounds = 10;
  const salt = await bcrypt.genSalt(saltRounds);
  this.password = await bcrypt.hash(this.password, salt);
  next();
});

//static method to login user
userSchema.statics.login = async function(email, password) {
  const user = await this.findOne({ email });
  if (!user) {
    throw Error('incorrect email!');
  }

  const auth = await bcrypt.compare(password, user.password);
  if (!auth) {
    throw Error('incorrect password!');
  }

  return user;
}
  /*const user = await this.findOne({ email });
  if (user){
    const auth = await bcrypt.compare(password, user.password);
    if (auth){
      return user;
    }
    throw Error('password didnt match!');
  }
  throw Error('incorrect email try again!');
}*/


const User = mongoose.model('user', userSchema);

module.exports = User;
