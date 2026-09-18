# Kupon Hesaplayıcı — GitHub'dan APK oluşturma

Bu proje 2, 3 ve 4 maç hesaplayıcılarını tek Android uygulamasında içerir. `.github/workflows/build-apk.yml` dosyası GitHub Actions üzerinde debug APK üretir.

## Telefonda / tarayıcıda yapılışı

1. GitHub'da yeni bir repository oluştur: `KuponHesaplayici`.
2. Repository'yi mümkünse **Public** yap; public repository'lerde GitHub-hosted Actions kullanımı ücretsiz ve sınırsızdır (GitHub'ın güncel runner politikasına göre).
3. ZIP'i bilgisayarda aç. GitHub repository sayfasında **Add file → Upload files** ile klasörün içindeki dosyaları yükle. `.github/workflows/build-apk.yml` dosyasının da yüklenmiş olduğundan emin ol.
4. **Commit changes** yap.
5. Repository'de **Actions** sekmesine gir. `Build Android APK` workflow'unu seç.
6. İstersen **Run workflow** ile manuel başlat; `main`/`master` branch'e push yapıldığında da otomatik çalışır.
7. Workflow yeşil olduğunda çalışmanın detayına gir. **Artifacts** bölümündeki `KuponHesaplayici-APK` dosyasını indir.
8. ZIP'i açıp `app-debug.apk` dosyasını Android telefona gönder ve kur.

## Not

Bu APK debug sürümüdür. Google Play Store'a yüklemek için ayrıca release signing gerekir. Telefona doğrudan kurup kullanmak için debug APK yeterlidir.
