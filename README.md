# NightGuard — Parent Night Mode App

## Kya karta hai
- Aap ek time window select karte hain (e.g. 7:00 PM se 11:00 PM tak)
- Us window ke andar phone ki **brightness** aur **volume** aapki set ki hui level par lock ho jati hai
- Agar bacha manually brightness/volume badhaye, service har ~4 second mein check karke wapas usi level par le aati hai
- App ke andar jaane ke liye **4-digit PIN** chahiye (sirf parent ko pata hoga)

## Android Studio mein Kholna
1. Android Studio open karein → **Open** → is `NightGuard` folder ko select karein
2. Gradle sync khud ho jayega (internet chahiye pehli dafa — dependencies download hongi)
3. Agar gradle wrapper na mile to Android Studio khud offer karega ise regenerate karne ka — accept kar dein

## Build karna
1. Menu se **Build → Build Bundle(s) / APK(s) → Build APK(s)**
2. Ya phone connect karke seedha **Run ▶** button dabayein (USB debugging on honi chahiye)
3. APK milega: `app/build/outputs/apk/debug/app-debug.apk`

## Pehli baar app chalane ke baad (zaroori steps)
1. App kholte hi 4-digit PIN set karne ko kahega — woh set karein
2. Settings screen mein **"Zaroori Permissions Check Karein"** button dabayein
3. **"Modify System Settings Allow Karein"** dabayein → Android ki settings screen khulegi → NightGuard ko **allow/on** kar dein → wapas app mein aayein
4. Ab Start Time, End Time, Brightness %, Volume % set karein
5. Switch **"Night Schedule On"** kar dein
6. **"Save aur Apply Karein"** dabayein

Bas — ab selected time window mein brightness/volume lock rahegi.

## Zaroori Notes
- Phone restart hone ke baad bhi schedule khud chalu ho jata hai (agar aap ne "Night Schedule On" chhoda hua ho) — `BootReceiver` ye kaam karta hai
- Kuch phones (Xiaomi, Vivo, Oppo) mein "Autostart" permission bhi manually allow karni padti hai (Settings → Apps → NightGuard → Autostart ON), warna phone restart ke baad service band ho sakti hai
- Kuch devices par Ringer volume set karne ke liye "Do Not Disturb access" bhi mangi ja sakti hai — agar aisa error aaye, code already usay silently ignore karta hai (crash nahi hoga), sirf ring volume enforce nahi hogi, baaki (media/system) theek chalega
- PIN bhool jayein to app uninstall + reinstall karke naya PIN set kiya ja sakta hai (abhi "forgot PIN" flow nahi hai)

## GitHub Actions se APK banana (Android Studio ke bina)
1. GitHub par ek naya repository banayein (public ya private, dono chalega)
2. Is poore `NightGuard` folder ka content us repo mein push/upload kar dein (`.github` folder bhi zaroor jaye)
3. GitHub par apne repo ke **Actions** tab par jayein
4. "Build NightGuard APK" workflow khud chalega jab aap `main` branch par push karenge — ya aap Actions tab se **Run workflow** button dabakar bhi manually chala sakte hain
5. Build complete hone ke baad, us workflow run ke andar niche **Artifacts** section mein `NightGuard-debug-apk` milega — usay download kar lein (zip ke andar `app-debug.apk` hoga)
6. Yeh APK phone mein install kar sakte hain (Settings mein "install unknown apps" allow karna padega)

## Future improvements (agar chahiye ho)
- "Forgot PIN" recovery option
- Multiple schedules (weekday/weekend alag)
- App usage blocking bhi (sirf brightness/volume nahi)
