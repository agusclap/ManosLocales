const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.notifyFavoritesOnNewProduct = functions.firestore
    .document("products/{productId}")
    .onCreate(async (snap, context) => {
      const product = snap.data();
      const providerId = product.providerId;

      const userSnapshots = await admin.firestore().collection("users").get();
      const tokens = [];

      const promises = userSnapshots.docs.map(async (doc) => {
        const favDoc = await doc.ref
            .collection("favoriteProviders")
            .doc(providerId)
            .get();
        if (favDoc.exists && doc.data().fcmToken) {
          tokens.push(doc.data().fcmToken);
        }
      });

      await Promise.all(promises);

      if (tokens.length > 0) {
        const message = {
          notification: {
            title: "¡Nuevo producto!",
            body: `${product.name} ya está disponible por $${product.price}`,
          },
          tokens: tokens,
        };
        return admin.messaging().sendMulticast(message);
      } else {
        return null;
      }
    });
