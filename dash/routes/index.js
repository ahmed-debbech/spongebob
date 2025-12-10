var express = require('express');
var router = express.Router();

var config = require("../config")
var axios = require("axios")

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'SpongeBob', google_redirect_uri: encodeURI(config.google_redirect_uri) });
});

router.get('/oauth', async function(req, res, next) {

  try{
    await axios({
      method: 'get',
      url: config.uploader_host + "/oauth?code=" + req.query.code
    })
  }catch(e){
    console.log("could not send code back from google to uploader service, it seems " + e);
      res.json({
        "authed" : false,
        "error" : e.toString()
      })
      return
  }

  res.json({
    "authed" : true
  })

});

module.exports = router;
