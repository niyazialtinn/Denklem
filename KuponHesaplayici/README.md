# Kupon Hesaplayıcı Android Uygulaması

Tek uygulamada 2, 3 ve 4 ayrı kupon için Excel'deki eşit geri dönüş hesabını yapar.

## Hesap
Her maç için yatırım:
`Kasa / (Oran × toplam(1/Oran))`

Böylece seçilen tüm maçlardan herhangi biri geldiğinde brüt geri dönüş aynı olur (yuvarlama hariç).

## APK oluşturma
1. Bilgisayara Android Studio kur.
2. Android Studio > Open ile bu klasörü aç.
3. Gradle senkronizasyonunun tamamlanmasını bekle.
4. Menüden Build > Build App Bundle(s) / APK(s) > Build APK(s) seç.
5. APK genellikle `app/build/outputs/apk/debug/app-debug.apk` altında oluşur.
6. APK'yı Android telefona gönderip aç. Android, bilinmeyen kaynaktan yükleme izni isterse ilgili izni ver.

## Not
Uygulama bahis oynatmaz; yalnızca kullanıcının girdiği kasa ve oranlar üzerinden matematiksel dağılım hesabı yapar.
