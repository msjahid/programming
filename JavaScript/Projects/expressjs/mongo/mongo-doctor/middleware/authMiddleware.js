const jwt = require('jsonwebtoken');
const User = require('../models/doctorModel');

const requireRoute = (req, res, next) => {
    const token = req.cookies.SSID;

    //check json web token exits & is verified
    if(token) {
        jwt.verify(token, 'secret key', (err, decodedToken) => {
            if(err) {
                res.redirect('/login');
            } else {
                console.log(decodedToken);
                next();
            }
        })
    } else {
        res.redirect('/login');
    }
}

const checkUser = (req, res, next) => {
    const token = req.cookies.SSID;

    if(token) {
        jwt.verify(token, 'secret key', async (err, decodedToken) => {
            if (err) {
                res.locals.currentUser = null;
                next();
            } else {
                let currentUser = await User.findById(decodedToken.id);
                res.locals.currentUser = currentUser;
                next();
            }
        })
    } else {
        res.locals.currentUser = null;
        next();
    }
}

module.exports = {requireRoute, checkUser};
