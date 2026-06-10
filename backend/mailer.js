const nodemailer = require('nodemailer');

const transporter = nodemailer.createTransport({
  service: 'gmail',
  auth: {
    user: process.env.EMAIL_USER,
    pass: process.env.EMAIL_PASS,
  }
});

const sendApprovalEmail = async (ownerEmail, propertyTitle) => {
  try {
    await transporter.sendMail({
      from: `"ToLetBD" <${process.env.EMAIL_USER}>`,
      to: ownerEmail,
      subject: 'আপনার বিজ্ঞাপন Approved হয়েছে!',
      html: `
        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
          <div style="background-color: #2E86AB; padding: 20px; text-align: center;">
            <h1 style="color: white; margin: 0;">🏠 ToLetBD</h1>
          </div>
          <div style="padding: 30px; background-color: #f9f9f9;">
            <h2 style="color: #333;">আপনার বিজ্ঞাপন Approved হয়েছে!</h2>
            <p style="color: #666; font-size: 16px;">
              আপনার <strong>"${propertyTitle}"</strong> বিজ্ঞাপনটি Admin কর্তৃক approve করা হয়েছে।
            </p>
            <p style="color: #666; font-size: 16px;">
              এখন সবাই আপনার বিজ্ঞাপন দেখতে পাবে।
            </p>
            <div style="text-align: center; margin-top: 30px;">
              <a href="http://localhost:3000/properties" 
                style="background-color: #2E86AB; color: white; padding: 12px 30px; 
                text-decoration: none; border-radius: 8px; font-size: 16px;">
                বিজ্ঞাপন দেখুন
              </a>
            </div>
          </div>
          <div style="padding: 20px; text-align: center; color: #999; font-size: 14px;">
            <p>ToLetBD — ঢাকার সেরা To-Let প্ল্যাটফর্ম</p>
          </div>
        </div>
      `
    });
    console.log('Approval email sent to:', ownerEmail);
  } catch (err) {
    console.log('Email error:', err.message);
  }
};

const sendRejectionEmail = async (ownerEmail, propertyTitle) => {
  try {
    await transporter.sendMail({
      from: `"ToLetBD" <${process.env.EMAIL_USER}>`,
      to: ownerEmail,
      subject: 'আপনার বিজ্ঞাপন Rejected হয়েছে',
      html: `
        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
          <div style="background-color: #2E86AB; padding: 20px; text-align: center;">
            <h1 style="color: white; margin: 0;">🏠 ToLetBD</h1>
          </div>
          <div style="padding: 30px; background-color: #f9f9f9;">
            <h2 style="color: #333;">আপনার বিজ্ঞাপন Rejected হয়েছে</h2>
            <p style="color: #666; font-size: 16px;">
              দুঃখিত, আপনার <strong>"${propertyTitle}"</strong> বিজ্ঞাপনটি Admin কর্তৃক reject করা হয়েছে।
            </p>
            <p style="color: #666; font-size: 16px;">
              তথ্য সঠিক করে আবার post করুন।
            </p>
            <div style="text-align: center; margin-top: 30px;">
              <a href="http://localhost:3000/post-property" 
                style="background-color: #F26419; color: white; padding: 12px 30px; 
                text-decoration: none; border-radius: 8px; font-size: 16px;">
                নতুন বিজ্ঞাপন দিন
              </a>
            </div>
          </div>
          <div style="padding: 20px; text-align: center; color: #999; font-size: 14px;">
            <p>ToLetBD — ঢাকার সেরা To-Let প্ল্যাটফর্ম</p>
          </div>
        </div>
      `
    });
    console.log('Rejection email sent to:', ownerEmail);
  } catch (err) {
    console.log('Email error:', err.message);
  }
};

module.exports = { sendApprovalEmail, sendRejectionEmail };