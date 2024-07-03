const User = require('../models/doctorModel');
const jwt = require('jsonwebtoken');


//handel errors
const handleErrors = (err) => {
  // console.log(err.message, err.code)
  let errors = { email: '', password: '' };

  //email error
  if(err.message === 'incorrect email!') {
    errors.email = "Invalid Email";
  }

  //password error
  if(err.message === 'incorrect password!') {
    errors.password = "Invalid Password";
  }

  //duplicate key error
  if(err.code === 11000) {
    errors.email = "Email already exists!";
    return errors;
  }

  //validate errors
  if(err.message.includes('user validation failed')){
    // console.log(err);
    //console.log(err.errors);
    //console.log(Object.values(err.errors));
    Object.values(err.errors).forEach(({ properties }) => {
      errors[properties.path] = properties.message;
    });
  }

  return errors;
};

// jwt token
const maxAge = 10 * 24 * 60 * 60;
const createToken = (id) => {
  return jwt.sign({ id }, 'secret key', {
    expiresIn: maxAge
  });
}

module.exports.signup_get = (req, res) => {
  res.render('pages/signup');
}

module.exports.login_get = (req, res) => {
  res.render('pages/login');
}

module.exports.signup_post = async (req, res) => {
  const {email, password} = req.body;

  try {
    const user = await User.create({email, password});
    const token = createToken(user._id);
    res.cookie('SSID', token, { httpOnly: true, maxAge: maxAge * 1000});
    res.status(201).json({ user: user._id });
  } catch (err) {
    const errors = handleErrors(err);
    // res.status(400).send('user not created!');
    res.status(400).json({ errors });
  }
}

// console.log(User.login('m@google.com', '12345678'));
module.exports.login_post = async (req, res) => {
  const { email, password } = req.body;

  try {
    const user = await User.login(email, password);
    const token = createToken(user._id);
    res.cookie('SSID', token, { httpOnly: true, maxAge: maxAge * 1000});
    res.status(200).json({ user: user._id });
  }catch (err){
    const errors = handleErrors(err);
    res.status(400).json({ errors });
  }
}

module.exports.logout_get = (req, res) => {
  res.cookie('SSID', '', { maxAge: 1 });
  res.redirect('/');
}