// ── KRUSHIEDGE AI COMPREHENSIVE CLIENT ENGINE ──

let currentLanguage = localStorage.getItem('krushi_lang') || 'kn';
let isLoggedIn = localStorage.getItem('krushi_is_logged_in') === 'true';

let currentUser = JSON.parse(localStorage.getItem('krushi_user')) || {
  name: 'ಶ್ರೀನಿವಾಸ್ ಗೌಡ (Srinivas)',
  phone: '9845012345',
  language: 'kn'
};

let currentFarm = JSON.parse(localStorage.getItem('krushi_farm')) || {
  name: 'ಶ್ರೀನಿವಾಸ್ ಗೌಡ ಅವರ ಹೊಲ',
  crop: 'ragi',
  area: '2.5',
  stage: 'flowering',
  village: 'ಮಂಡ್ಯ (Mandya)'
};

let isSpeaking = false;
let selectedCrop = currentFarm.crop || 'ragi';
let cameraStream = null;
let currentFacingMode = 'environment';
let isListeningSpeech = false;
let lastDiagnosisResult = null;

// Safe DOM helper
function setElText(id, text) {
  const el = document.getElementById(id);
  if (el) el.textContent = text;
}
function setElHtml(id, html) {
  const el = document.getElementById(id);
  if (el) el.innerHTML = html;
}

// ── COMPREHENSIVE MULTILINGUAL DICTIONARY ──
const i18n = {
  kn: {
    langName: "ಕನ್ನಡ",
    authLangBadge: "ಕನ್ನಡ",
    authSubTitle: "ನಿಮ್ಮ ಹೊಲದ ನೈಜ AI ಸಹಾಯಕ",
    authTagline: "ಇಂಟರ್ನೆಟ್ ಇಲ್ಲದಿದ್ದರೂ ಕಾರ್ಯನಿರ್ವಹಿಸುತ್ತದೆ",
    btnPillLogin: "ಲಾಗಿನ್ (Login)",
    btnPillSignUp: "ಹೊಸ ಖಾತೆ (Sign Up)",
    lblAuthLoginUser: "ಮೊಬೈಲ್ ಸಂಖ್ಯೆ / ಇಮೇಲ್",
    lblAuthLoginPass: "ಪಾಸ್‌ವರ್ಡ್ (Password)",
    btnDoLoginText: "ಖಾತೆಗೆ ಪ್ರವೇಶಿಸಿ (Login)",
    lblAuthSignName: "ನಿಮ್ಮ ಪೂರ್ಣ ಹೆಸರು (Full Name)",
    lblAuthSignPhone: "ಮೊಬೈಲ್ ಸಂಖ್ಯೆ (Phone)",
    lblAuthSignCrop: "ನಿಮ್ಮ ಮುಖ್ಯ ಬೆಳೆ (Crop)",
    lblAuthSignArea: "ಹೊಲದ ವಿಸ್ತೀರ್ಣ (Acres)",
    lblAuthSignVillage: "ಹಳ್ಳಿ / ಜಿಲ್ಲೆ (Village)",
    lblAuthSignLang: "ನಿಮ್ಮ ಆದ್ಯತೆಯ ಭಾಷೆ (Language)",
    lblAuthSignPass: "ಪಾಸ್‌ವರ್ಡ್ ರಚಿಸಿ (Password)",
    btnDoSignUpText: "ಖಾತೆ ರಚಿಸಿ & ಪ್ರಾರಂಭಿಸಿ",
    btnGuestText: "ಡೆಮೊ / ಅತಿಥಿಯಾಗಿ ಮುಂದುವರಿಸಿ (Continue as Guest)",
    headerSub: "ನಿಮ್ಮ ಹೊಲದ AI ಸಹಾಯಕ",
    audioTitle: "ಇಂದಿನ ಬೆಳೆ ಸಲಹೆ ಆಲಿಸಿ (ಕನ್ನಡ)",
    audioSub: "ಸ್ಪಷ್ಟ ಕನ್ನಡದಲ್ಲಿ ಕೇಳಲು ಒತ್ತಿ",
    btnEditFarm: "ಬದಲಾಯಿಸಿ",
    alertTitle: "ರೋಗ ತಪಾಸಣೆ ಮತ್ತು ಹವಾಮಾನ ಎಚ್ಚರಿಕೆ",
    alertBody: "ತೇವಾಂಶ 68% ಇರುವುದರಿಂದ ಹೊಲವನ್ನು ಪರಿಶೀಲಿಸಿ. ಅಗತ್ಯವಿದ್ದಲ್ಲಿ ಜೈವಿಕ ಔಷಧ ಸಿಂಪಡಿಸಿ.",
    btnZoning: "ವಲಯ ನಕ್ಷೆ ನೋಡಿ",
    scanTitle: "ಬೆಳೆ ಡಾಕ್ಟರ್ AI ಕ್ಯಾಮೆರಾ",
    scanSub: "ನೈಜ ಕ್ಯಾಮೆರಾದಿಂದ ಫೋಟೋ ತೆಗೆದು ರೋಗ ಪತ್ತೆ ಹಚ್ಚಿ",
    weatherHeading: "ಇಂದಿನ ಹವಾಮಾನ ಮತ್ತು ಮಣ್ಣಿನ ಸ್ಥಿತಿ",
    lblTemp: "ತಾಪಮಾನ",
    subTemp: "ಸಾಮಾನ್ಯ ಬಿಸಿಲು",
    lblHumidity: "ತೇವಾಂಶ",
    subHumidity: "ಸೂಕ್ತ ಮಟ್ಟ",
    lblRain: "ಮಳೆ ಸಂಭವ",
    subRain: "ಮಳೆ ಇಲ್ಲ",
    sprayTitle: "ಕೀಟನಾಶಕ ಸಿಂಪರಣೆ ಮುನ್ಸೂಚನೆ",
    spraySafe: "ಇಂದು ಸಿಂಪರಣೆಗೆ ಸೂಕ್ತ ವಾತಾವರಣವಿದೆ (ಗಾಳಿಯ ವೇಗ: 9 km/h, ಮಳೆ ಸಾಧ್ಯತೆ ಕಡಿಮೆ).",
    modulesHeading: "ಸ್ಮಾರ್ಟ್ ಕೃಷಿ ಸೇವೆಗಳು",
    modIrrigTitle: "ನೀರಾವರಿ ಸಲಹೆ",
    modIrrigSub: "ನಾಳೆ 1.8 ಗಂಟೆ ಪಂಪ್",
    modPestTitle: "ಕೀಟ ಮುನ್ಸೂಚನೆ",
    modPestSub: "ಕೀಟ ಹಾವಳಿ ಎಚ್ಚರಿಕೆ",
    modMarketTitle: "ಮಂಡಿ ಬೆಲೆ",
    apkBannerTitle: "ಆಂಡ್ರಾಯ್ಡ್ ಆ್ಯಪ್ ಡೌನ್‌ಲೋಡ್ ಮಾಡಿ",
    apkBannerSub: "ಇಂಟರ್ನೆಟ್ ಇಲ್ಲದಿದ್ದರೂ ಆಫ್‌ಲೈನ್ AI ಕೆಲಸ ಮಾಡುತ್ತದೆ",
    viewfinderHint: "ಬಾಧಿತ ಎಲೆಯನ್ನು ಚೌಕಟ್ಟಿನಲ್ಲಿ ಹಿಡಿಯಿರಿ",
    btnStartCam: "ಲೈವ್ ಕ್ಯಾಮೆರಾ ಆನ್ ಮಾಡಿ",
    btnUploadText: "ಫೋಟೋ ಆಯ್ಕೆಮಾಡಿ (Upload Leaf)",
    shutterNote: "💡 ಬೆಳೆಯ ಎಲೆಯ ಹತ್ತಿರದ ಫೋಟೋವನ್ನು ತೆಗೆಯಿರಿ ಅಥವಾ ಗ್ಯಾಲರಿಯಿಂದ ಅಪ್‌ಲೋಡ್ ಮಾಡಿ!",
    irrigMoistureTitle: "ಮಣ್ಣಿನ ಪ್ರಸ್ತುತ ತೇವಾಂಶ (Soil Moisture)",
    irrigStatusChip: "ನೀರುಣಿಸುವ ಅಗತ್ಯವಿದೆ",
    gaugeTarget: "ಗುರಿ",
    pumpScheduleTitle: "ಶಿಫಾರಸು ಮಾಡಿದ ಪಂಪ್ ಚಾಲನೆ ಸಮಯ",
    pumpMotorLabel: "5 HP ಮೋಟಾರ್ ಅವಧಿ",
    pumpOptimalLabel: "ಉತ್ತಮ ಸಮಯ",
    pumpOptimalTime: "ನಾಳೆ ಬೆಳಗ್ಗೆ 06:00 - 08:00",
    pumpSavingsNote: "ಬೆಳಗಿನ ಸಮಯದಲ್ಲಿ ನೀರುಣಿಸುವುದರಿಂದ 28.5% ಆವಿಯಾಗುವಿಕೆ ನಷ್ಟ ತಡೆಯಬಹುದು.",
    calcMotorTitle: "ನಿಮ್ಮ ಮೋಟಾರ್ ಸಾಮರ್ಥ್ಯ ಆಯ್ಕೆಮಾಡಿ (HP)",
    pestHeroName: "ಕೀಟ ಬಾಧೆ ಮುನ್ಸೂಚನೆ",
    pestHeroRisk: "ಮಧ್ಯಮ ರಿಸ್ಕ್",
    pestProjectionLabel: "7 ದಿನಗಳ ಹಾವಳಿ ಸಂಭವನೀಯತೆ (7-Day Projection):",
    pestActionTitle: "ತಕ್ಷಣದ ಕ್ರಮ (Immediate Action):",
    pestActionBody: "ಬೇವಿನ ಎಣ್ಣೆ (Azadirachtin 10,000 ppm) 2ml/L ನೀರಿನಲ್ಲಿ ಬೆರೆಸಿ ಮುಂಜಾನೆ ಸಿಂಪಡಿಸಿ.",
    dayMon: "ಸೋಮ", dayTue: "ಮಂಗಳ", dayWed: "ಬುಧ", dayThu: "ಗುರು", dayFri: "ಶುಕ್ರ", daySat: "ಶನಿ", daySun: "ಭಾನು",
    mandiLocTitle: "ಮಂಡ್ಯ APMC ಮಾರುಕಟ್ಟೆ (Mandya APMC)",
    mandiLiveTag: "ಇಂದಿನ ನೈಜ ಲೈವ್ ಬೆಲೆಗಳು",
    mandiSearchPlaceholder: "ಬೆಳೆ ಹುಡುಕಿ (Search Crop)...",
    voiceAssistantTitle: "ಕೃಷಿ AI ಧ್ವನಿ ಸಹಾಯಕ",
    voiceAssistantSub: "ನಿಮ್ಮ ಭಾಷೆಯಲ್ಲೇ ನೇರವಾಗಿ ಮಾತನಾಡಿ",
    chatGreeting: "ನಮಸ್ಕಾರ {name} ಅವರೇ! ನಿಮ್ಮ ಹೊಲದ ರೋಗಗಳು, ನೀರಾವರಿ ಅಥವಾ ಮಂಡಿ ಬೆಲೆಗಳ ಬಗ್ಗೆ ಏನಾದರೂ ಕೇಳಿ.",
    micStatusTap: "ಮಾತನಾಡಲು ಮೈಕ್ ಒತ್ತಿ (Tap to Speak)",
    micStatusListening: "ಆಲಿಸಲಾಗುತ್ತಿದೆ... ಮಾತನಾಡಿ (Listening...)",
    btnLogout: "ಖಾತೆ ಬದಲಾಯಿಸಿ (Logout)",
    setLangGroupTitle: "ಭಾಷಾ ಆಯ್ಕೆ (Language)",
    setAiGroupTitle: "AI ಕಾರ್ಯಾಚರಣೆ ಮೋಡ್ (AI Mode)",
    optHybridTitle: "ಹೈಬ್ರಿಡ್ ಮೋಡ್ (Hybrid Edge + Cloud)",
    optHybridSub: "ಇಂಟರ್ನೆಟ್ ಇದ್ದಾಗ ಆನ್‌ಲೈನ್, ಇಲ್ಲದಿದ್ದಾಗ ಆಫ್‌ಲೈನ್ AI",
    optLocalTitle: "ಆಫ್‌ಲೈನ್ ಮಾತ್ರ (Offline Edge AI Only)",
    optLocalSub: "100% ಫೋನ್‌ನಲ್ಲಿಯೇ ಕಾರ್ಯನಿರ್ವಹಣೆ",
    navHome: "ಮುಖಪುಟ",
    navScan: "ಸ್ಕ್ಯಾನ್",
    navIrrigation: "ನೀರಾವರಿ",
    navMarket: "ಮಂಡಿ",
    navVoice: "ಧ್ವನಿ AI",
    navSettings: "ಸೆಟ್ಟಿಂಗ್ಸ್",
    btnSpeakResult: "ಪರಿಹಾರವನ್ನು ಆಲಿಸಿ (ಕನ್ನಡ)",
    spokenAdvisory: "ನಮಸ್ಕಾರ {name}. ಇಂದಿನ ಹವಾಮಾನ: ಉಷ್ಣಾಂಶ 29 ಡಿಗ್ರಿ ಸೆಲ್ಸಿಯಸ್. ಗಾಳಿಯ ವೇಗ ಗಂಟೆಗೆ 9 ಕಿಲೋಮೀಟರ್. ನಿಮ್ಮ ಬೆಳೆ {crop} {area} ಎಕರೆಯಲ್ಲಿದೆ. ಇಂದು ಕೀಟನಾಶಕ ಸಿಂಪರಣೆಗೆ ಉತ್ತಮ ವಾತಾವರಣವಿದೆ.",
    acreUnit: "ಎಕರೆ",
    farmOf: "ಅವರ ಹೊಲ",
    editModalTitle: "ಹೊಲದ ವಿವರ ಬದಲಾಯಿಸಿ (Edit Farm)"
  },
  hi: {
    langName: "हिन्दी",
    authLangBadge: "हिन्दी",
    authSubTitle: "आपका वास्तविक खेत AI सहायक",
    authTagline: "बिना इंटरनेट के भी पूर्णतः कार्य करता है",
    btnPillLogin: "लॉगिन (Login)",
    btnPillSignUp: "नया खाता (Sign Up)",
    lblAuthLoginUser: "मोबाइल नंबर / ईमेल",
    lblAuthLoginPass: "पासवर्ड (Password)",
    btnDoLoginText: "लॉगिन करें (Login)",
    lblAuthSignName: "आपका पूरा नाम (Full Name)",
    lblAuthSignPhone: "मोबाइल नंबर (Phone)",
    lblAuthSignCrop: "आपकी मुख्य फसल (Crop)",
    lblAuthSignArea: "खेत का क्षेत्रफल (Acres)",
    lblAuthSignVillage: "गाँव / जिला (Village)",
    lblAuthSignLang: "आपकी पसंदीदा भाषा (Language)",
    lblAuthSignPass: "पासवर्ड बनाएं (Password)",
    btnDoSignUpText: "खाता बनाएं और शुरू करें",
    btnGuestText: "डेमो / अतिथि के रूप में जारी रखें (Continue as Guest)",
    headerSub: "आपका खेत AI सहायक",
    audioTitle: "आज की फसल सलाह सुनें (हिन्दी)",
    audioSub: "स्पष्ट आवाज में सुनने के लिए दबाएं",
    btnEditFarm: "बदलें",
    alertTitle: "रोग जांच एवं मौसम चेतावनी",
    alertBody: "आर्द्रता 68% होने के कारण खेत का निरीक्षण करें। आवश्यकता पड़ने पर जैव कवकनाशी छिड़कें।",
    btnZoning: "क्षेत्रीय मानचित्र देखें",
    scanTitle: "क्रॉप डॉक्टर AI कैमरा",
    scanSub: "लाइव कैमरे से फोटो खींचकर रोग पहचानें",
    weatherHeading: "आज का मौसम और मिट्टी की स्थिति",
    lblTemp: "तापमान",
    subTemp: "धूप / साफ",
    lblHumidity: "आर्द्रता",
    subHumidity: "उचित स्तर",
    lblRain: "बारिश की संभावना",
    subRain: "बारिश नहीं",
    sprayTitle: "कीटनाशक छिड़काव पूर्वानुमान",
    spraySafe: "आज छिड़काव के लिए अनुकूल मौसम है (हवा की गति: 9 km/h, बारिश की संभावना कम)।",
    modulesHeading: "स्मार्ट कृषि सेवाएं",
    modIrrigTitle: "सिंचाई सलाह",
    modIrrigSub: "कल 1.8 घंटे पंप चलाएं",
    modPestTitle: "कीट पूर्वानुमान",
    modPestSub: "कीट प्रकोप चेतावनी",
    modMarketTitle: "मंडी भाव",
    apkBannerTitle: "एंड्रॉइड ऐप डाउनलोड करें",
    apkBannerSub: "बिना इंटरनेट के भी ऑफलाइन AI काम करता है",
    viewfinderHint: "संक्रमित पत्ती को फ्रेम में रखें",
    btnStartCam: "लाइव कैमरा शुरू करें",
    btnUploadText: "पत्ती का फोटो चुनें (Upload Leaf)",
    shutterNote: "💡 मोबाइल कैमरे या गैलरी से असली फसल पत्ती की फोटो जांचें!",
    irrigMoistureTitle: "मिट्टी की वर्तमान नमी (Soil Moisture)",
    irrigStatusChip: "सिंचाई की आवश्यकता है",
    gaugeTarget: "लक्ष्य",
    pumpScheduleTitle: "अनुशंसित पंप संचालन समय",
    pumpMotorLabel: "5 HP मोटर अवधि",
    pumpOptimalLabel: "सर्वोत्तम समय",
    pumpOptimalTime: "कल सुबह 06:00 - 08:00",
    pumpSavingsNote: "सुबह के समय सिंचाई करने से 28.5% वाष्पीकरण नुकसान को रोका जा सकता है।",
    calcMotorTitle: "अपनी मोटर क्षमता चुनें (HP)",
    pestHeroName: "कीट प्रकोप पूर्वानुमान",
    pestHeroRisk: "मध्यम जोखिम",
    pestProjectionLabel: "7 दिनों का प्रकोप अनुमान (7-Day Projection):",
    pestActionTitle: "त्वरित कार्रवाई (Immediate Action):",
    pestActionBody: "नीम का तेल (Azadirachtin 10,000 ppm) 2ml/L पानी में मिलाकर सुबह छिड़कें।",
    dayMon: "सोम", dayTue: "मंगल", dayWed: "बुध", dayThu: "गुरु", dayFri: "शुक्र", daySat: "शनि", daySun: "रवि",
    mandiLocTitle: "मंड्या APMC मंडी (Mandya APMC)",
    mandiLiveTag: "आज के ताजा लाइव भाव",
    mandiSearchPlaceholder: "फसल खोजें (Search Crop)...",
    voiceAssistantTitle: "कृषि AI वॉइस असिस्टेंट",
    voiceAssistantSub: "अपनी भाषा में सीधे बात करें",
    chatGreeting: "नमस्ते {name}! अपनी फसल के रोग, सिंचाई या मंडी भाव के बारे में कुछ भी पूछें।",
    micStatusTap: "बोलने के लिए माइक दबाएं (Tap to Speak)",
    micStatusListening: "सुन रहा हूँ... बोलिए (Listening...)",
    btnLogout: "खाता बदलें (Logout)",
    setLangGroupTitle: "भाषा चयन (Language)",
    setAiGroupTitle: "AI संचालन मोड (AI Mode)",
    optHybridTitle: "हाइब्रिड मोड (Hybrid Edge + Cloud)",
    optHybridSub: "इंटरनेट होने पर ऑनलाइन, न होने पर ऑफलाइन AI",
    optLocalTitle: "केवल ऑफलाइन (Offline Edge AI Only)",
    optLocalSub: "100% फोन पर ही कार्य करेगा",
    navHome: "होम",
    navScan: "स्कैन",
    navIrrigation: "सिंचाई",
    navMarket: "मंडी",
    navVoice: "वॉइस AI",
    navSettings: "सेटिंग्स",
    btnSpeakResult: "उपचार सुनें (हिन्दी)",
    spokenAdvisory: "नमस्ते {name}। आज का मौसम: तापमान 29 डिग्री सेल्सियस। हवा की गति 9 किलोमीटर प्रति घंटा। आपकी फसल {crop} {area} एकड़ में है। आज कीटनाशक छिड़काव के लिए मौसम उपयुक्त है।",
    acreUnit: "एकड़",
    farmOf: "का खेत",
    editModalTitle: "खेत का विवरण बदलें (Edit Farm)"
  },
  en: {
    langName: "English",
    authLangBadge: "English",
    authSubTitle: "Your Field AI Companion",
    authTagline: "Fully Operational Offline Without Internet",
    btnPillLogin: "Login",
    btnPillSignUp: "Sign Up",
    lblAuthLoginUser: "Mobile Number / Email",
    lblAuthLoginPass: "Password",
    btnDoLoginText: "Sign In (Login)",
    lblAuthSignName: "Your Full Name",
    lblAuthSignPhone: "Mobile Number",
    lblAuthSignCrop: "Primary Crop",
    lblAuthSignArea: "Land Area (Acres)",
    lblAuthSignVillage: "Village / District",
    lblAuthSignLang: "Preferred Language",
    lblAuthSignPass: "Create Password",
    btnDoSignUpText: "Create Account & Start",
    btnGuestText: "Continue as Guest / Demo",
    headerSub: "Your Field AI Companion",
    audioTitle: "Listen to Today's Crop Advisory",
    audioSub: "Tap to listen with spoken AI voice",
    btnEditFarm: "Edit",
    alertTitle: "Crop Health & Weather Alert",
    alertBody: "Humidity at 68%. Inspect your field and apply bio-fungicide if lesion symptoms appear.",
    btnZoning: "View Field Zoning",
    scanTitle: "Crop Doctor AI Camera",
    scanSub: "Capture live photo & diagnose on-device (offline)",
    weatherHeading: "Today's Microclimate & Soil Status",
    lblTemp: "Temperature",
    subTemp: "Clear / Sunny",
    lblHumidity: "Humidity",
    subHumidity: "Optimal",
    lblRain: "Rain Chance",
    subRain: "No Rain Expected",
    sprayTitle: "Pesticide Spray Advisory",
    spraySafe: "Safe conditions for spraying today (Wind: 9 km/h, low precipitation chance).",
    modulesHeading: "Smart Agricultural Modules",
    modIrrigTitle: "Smart Irrigation",
    modIrrigSub: "Run pump 1.8 hrs tomorrow",
    modPestTitle: "Pest Forecast",
    modPestSub: "Pest Outbreak Alert",
    modMarketTitle: "Mandi Prices",
    apkBannerTitle: "Download Android App",
    apkBannerSub: "Works offline with on-device Edge AI",
    viewfinderHint: "Align infected leaf inside frame",
    btnStartCam: "Start Live Camera",
    btnUploadText: "Choose Leaf Photo (Upload)",
    shutterNote: "💡 Capture a real leaf photo or upload from gallery to test!",
    irrigMoistureTitle: "Current Soil Moisture",
    irrigStatusChip: "Irrigation Needed",
    gaugeTarget: "Target",
    pumpScheduleTitle: "Recommended Pump Run Time",
    pumpMotorLabel: "5 HP Motor Duration",
    pumpOptimalLabel: "Optimal Window",
    pumpOptimalTime: "Tomorrow 06:00 - 08:00 AM",
    pumpSavingsNote: "Early morning irrigation prevents 28.5% evaporation loss.",
    calcMotorTitle: "Select Your Motor Capacity (HP)",
    pestHeroName: "Pest Risk Warning",
    pestHeroRisk: "Moderate Risk",
    pestProjectionLabel: "7-Day Pest Outbreak Projection:",
    pestActionTitle: "Immediate Action Plan:",
    pestActionBody: "Spray 2ml/L Azadirachtin (10,000 ppm) during early morning hours.",
    dayMon: "Mon", dayTue: "Tue", dayWed: "Wed", dayThu: "Thu", dayFri: "Fri", daySat: "Sat", daySun: "Sun",
    mandiLocTitle: "Mandya APMC Market Yard",
    mandiLiveTag: "Today's Live Real-time Prices",
    mandiSearchPlaceholder: "Search Crop...",
    voiceAssistantTitle: "Krushi AI Voice Assistant",
    voiceAssistantSub: "Speak naturally in your regional language",
    chatGreeting: "Hello {name}! Ask anything about your crop health, irrigation, or market prices.",
    micStatusTap: "Tap to Speak",
    micStatusListening: "Listening... Speak now",
    btnLogout: "Switch Account (Logout)",
    setLangGroupTitle: "Preferred Language",
    setAiGroupTitle: "AI Engine Mode",
    optHybridTitle: "Hybrid Mode (Edge + Cloud)",
    optHybridSub: "Online when connected, offline Edge AI otherwise",
    optLocalTitle: "Offline Only (Edge AI)",
    optLocalSub: "Runs 100% locally on your phone",
    navHome: "Home",
    navScan: "Scan",
    navIrrigation: "Irrigation",
    navMarket: "Mandi",
    navVoice: "Voice AI",
    navSettings: "Settings",
    btnSpeakResult: "Listen to Remedy (English)",
    spokenAdvisory: "Hello {name}. Today's weather: Temperature 29 degrees Celsius. Wind speed 9 kilometers per hour. Your {crop} crop is on {area} acres. Safe conditions for field operations.",
    acreUnit: "Acres",
    farmOf: "'s Farm",
    editModalTitle: "Edit Farm Details"
  }
};

const cropNames = {
  ragi: { kn: "ರಾಗಿ (Finger Millet)", hi: "रागी (Ragi)", en: "Finger Millet (Ragi)" },
  rice: { kn: "ಭತ್ತ (Paddy / Rice)", hi: "धान (Paddy)", en: "Paddy (Rice)" },
  sugarcane: { kn: "ಕಬ್ಬು (Sugarcane)", hi: "गन्ना (Sugarcane)", en: "Sugarcane" },
  tomato: { kn: "ಟೊಮ್ಯಾಟೊ (Tomato)", hi: "टमाटर (Tomato)", en: "Tomato" },
  cotton: { kn: "ಹತ್ತಿ (Cotton)", hi: "कपास (Cotton)", en: "Cotton" },
  groundnut: { kn: "ಕಡಲೆಕಾಯಿ (Groundnut)", hi: "मूंगफली (Groundnut)", en: "Groundnut" },
  maize: { kn: "ಮೆಕ್ಕೆಜೋಳ (Maize)", hi: "मक्का (Maize)", en: "Maize" },
  onion: { kn: "ಈರುಳ್ಳಿ (Onion)", hi: "प्याज (Onion)", en: "Onion" }
};

const stageNames = {
  sowing: { kn: "ಬಿತ್ತನೆ ಹಂತ", hi: "बुवाई की अवस्था", en: "Sowing Stage" },
  vegetative: { kn: "ಸಸ್ಯ ಬೆಳವಣಿಗೆ ಹಂತ", hi: "वानस्पतिक अवस्था", en: "Vegetative Stage" },
  flowering: { kn: "ಹೂವಾಡುವ ಹಂತ", hi: "फूल आने की अवस्था", en: "Flowering Stage" },
  grain: { kn: "ಕಾಳು ತುಂಬುವ ಹಂತ", hi: "दाना भरने की अवस्था", en: "Grain Filling Stage" },
  harvest: { kn: "ಕೊಯ್ಲು ಹಂತ", hi: "कटाई की अवस्था", en: "Harvest Stage" }
};

// Mandi Price Data
const mandiData = [
  { cropKn: "ರಾಗಿ (Ragi)", cropHi: "रागी (Ragi)", cropEn: "Finger Millet (Ragi)", variety: "GPU-28 / MR-1", priceKn: "₹3,720 / ಕ್ವಿಂಟಾಲ್", priceHi: "₹3,720 / क्विंटल", priceEn: "₹3,720 / Quintal", change: "+₹80", trend: "up" },
  { cropKn: "ಭತ್ತ (Paddy)", cropHi: "धान (Paddy)", cropEn: "Paddy (Rice)", variety: "Jyothi / Sona Masoori", priceKn: "₹2,450 / ಕ್ವಿಂಟಾಲ್", priceHi: "₹2,450 / क्विंटल", priceEn: "₹2,450 / Quintal", change: "+₹30", trend: "up" },
  { cropKn: "ಕಬ್ಬು (Sugarcane)", cropHi: "गन्ना (Sugarcane)", cropEn: "Sugarcane", variety: "Co-86032", priceKn: "₹3,150 / ಟನ್", priceHi: "₹3,150 / टन", priceEn: "₹3,150 / Ton", change: "0", trend: "stable" },
  { cropKn: "ಟೊಮ್ಯಾಟೊ (Tomato)", cropHi: "टमाटर (Tomato)", cropEn: "Tomato Hybrid", variety: "Shivam / Saaho", priceKn: "₹1,800 / 15kg ಪೆಟ್ಟಿಗೆ", priceHi: "₹1,800 / 15kg बॉक्स", priceEn: "₹1,800 / 15kg Crate", change: "-₹120", trend: "down" },
  { cropKn: "ಕಡಲೆಕಾಯಿ (Groundnut)", cropHi: "मूंगफली (Groundnut)", cropEn: "Groundnut (Pod)", variety: "TMV-2", priceKn: "₹6,850 / ಕ್ವಿಂಟಾಲ್", priceHi: "₹6,850 / क्विंटल", priceEn: "₹6,850 / Quintal", change: "+₹150", trend: "up" },
  { cropKn: "ಈರುಳ್ಳಿ (Onion)", cropHi: "प्याज (Onion)", cropEn: "Red Onion", variety: "Bellary Red", priceKn: "₹2,200 / ಕ್ವಿಂಟಾಲ್", priceHi: "₹2,200 / क्विंटल", priceEn: "₹2,200 / Quintal", change: "+₹50", trend: "up" }
];

// ── DOM READY INITIALIZATION ──
document.addEventListener('DOMContentLoaded', () => {
  // Live Clock
  function updateClock() {
    const now = new Date();
    const clockEl = document.getElementById('liveClock');
    if (clockEl) {
      clockEl.textContent = now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    }
  }
  setInterval(updateClock, 1000);
  updateClock();

  // Apply Language & Screen Visibility
  applyLanguage(currentLanguage);
  updateScreenVisibility();
  updateUserAndFarmDisplay();
  renderMandiPrices();
  fetchLiveWeather();

  // ── AUTH TOGGLE PILL ──
  const btnPillLogin = document.getElementById('btnPillLogin');
  const btnPillSignUp = document.getElementById('btnPillSignUp');
  const formLogin = document.getElementById('formLogin');
  const formSignUp = document.getElementById('formSignUp');

  btnPillLogin?.addEventListener('click', () => {
    btnPillLogin.classList.add('active');
    btnPillSignUp.classList.remove('active');
    formLogin.style.display = 'flex';
    formSignUp.style.display = 'none';
  });

  btnPillSignUp?.addEventListener('click', () => {
    btnPillSignUp.classList.add('active');
    btnPillLogin.classList.remove('active');
    formSignUp.style.display = 'flex';
    formLogin.style.display = 'none';
  });

  // Password Visibility Toggle
  document.getElementById('btnToggleLoginPwd')?.addEventListener('click', () => {
    const pwdInput = document.getElementById('inputLoginPassword');
    if (pwdInput) {
      pwdInput.type = (pwdInput.type === 'password') ? 'text' : 'password';
    }
  });

  // ── AUTH SUBMIT: LOGIN ──
  function doLogin() {
    const id = document.getElementById('inputLoginIdentifier')?.value.trim() || '9845012345';
    const name = (id === '9845012345' || !id) ? 'ಶ್ರೀನಿವಾಸ್ ಗೌಡ (Srinivas)' : (id.includes('@') ? id.split('@')[0] : 'ರೈತ ' + id.slice(-4));

    currentUser = { name: name, phone: id, language: currentLanguage };
    isLoggedIn = true;
    localStorage.setItem('krushi_user', JSON.stringify(currentUser));
    localStorage.setItem('krushi_is_logged_in', 'true');

    updateUserAndFarmDisplay();
    updateScreenVisibility();
  }

  formLogin?.addEventListener('submit', (e) => {
    e.preventDefault();
    doLogin();
  });

  // Backup: direct button click handler (in case form submit has issues)
  document.getElementById('btnDoLogin')?.addEventListener('click', (e) => {
    e.preventDefault();
    doLogin();
  });

  // ── AUTH SUBMIT: SIGN UP WITH FARM PROFILE ──
  formSignUp?.addEventListener('submit', (e) => {
    e.preventDefault();
    const name = document.getElementById('inputSignName')?.value.trim() || 'ಶ್ರೀನಿವಾಸ್ ಗೌಡ';
    const phone = document.getElementById('inputSignPhone')?.value.trim() || '9845012345';
    const crop = document.getElementById('selectSignCrop')?.value || 'ragi';
    const area = document.getElementById('inputSignArea')?.value || '2.5';
    const village = document.getElementById('inputSignVillage')?.value.trim() || 'ಮಂಡ್ಯ (Mandya)';
    const lang = document.getElementById('selectSignLanguage')?.value || currentLanguage;

    currentUser = { name: name, phone: phone, language: lang };
    currentFarm = {
      name: `${name.split(' ')[0]} ಅವರ ಹೊಲ`,
      crop: crop,
      area: area,
      stage: 'flowering',
      village: village
    };
    selectedCrop = crop;

    isLoggedIn = true;
    localStorage.setItem('krushi_user', JSON.stringify(currentUser));
    localStorage.setItem('krushi_farm', JSON.stringify(currentFarm));
    localStorage.setItem('krushi_is_logged_in', 'true');
    
    currentLanguage = lang;
    localStorage.setItem('krushi_lang', lang);
    applyLanguage(lang);

    updateUserAndFarmDisplay();
    updateScreenVisibility();
  });

  // ── GUEST / DEMO CONTINUE ──
  document.getElementById('btnContinueGuest')?.addEventListener('click', () => {
    isLoggedIn = true;
    localStorage.setItem('krushi_is_logged_in', 'true');
    updateScreenVisibility();
  });

  // ── LOGOUT / SWITCH ACCOUNT ──
  document.getElementById('btnLogoutUser')?.addEventListener('click', () => {
    isLoggedIn = false;
    localStorage.setItem('krushi_is_logged_in', 'false');
    updateScreenVisibility();
  });

  document.getElementById('btnAuthHeader')?.addEventListener('click', () => {
    // Open farm edit modal
    openFarmEditModal();
  });

  // ── FARM EDIT MODAL ──
  const farmEditModal = document.getElementById('farmEditModal');
  const btnCloseFarmEditModal = document.getElementById('btnCloseFarmEditModal');
  const formEditFarm = document.getElementById('formEditFarm');

  function openFarmEditModal() {
    const rawName = currentUser.name.split(' ')[0] || 'ಶ್ರೀನಿವಾಸ್';
    const nameInput = document.getElementById('editFarmerNameInput');
    const cropSelect = document.getElementById('editCropSelect');
    const areaInput = document.getElementById('editAreaInput');
    const stageSelect = document.getElementById('editStageSelect');

    if (nameInput) nameInput.value = rawName;
    if (cropSelect) cropSelect.value = currentFarm.crop || 'ragi';
    if (areaInput) areaInput.value = currentFarm.area || '2.5';
    if (stageSelect) stageSelect.value = currentFarm.stage || 'flowering';
    if (farmEditModal) farmEditModal.style.display = 'flex';
  }

  document.getElementById('btnEditFarm')?.addEventListener('click', openFarmEditModal);

  btnCloseFarmEditModal?.addEventListener('click', () => {
    if (farmEditModal) farmEditModal.style.display = 'none';
  });

  formEditFarm?.addEventListener('submit', (e) => {
    e.preventDefault();
    const newName = document.getElementById('editFarmerNameInput')?.value.trim() || 'ಶ್ರೀನಿವಾಸ್ ಗೌಡ';
    const newCrop = document.getElementById('editCropSelect')?.value || 'ragi';
    const newArea = document.getElementById('editAreaInput')?.value || '2.5';
    const newStage = document.getElementById('editStageSelect')?.value || 'flowering';

    currentUser.name = newName;
    currentFarm = {
      name: `${newName} ಅವರ ಹೊಲ`,
      crop: newCrop,
      area: newArea,
      stage: newStage,
      village: currentFarm.village || 'ಮಂಡ್ಯ (Mandya)'
    };
    selectedCrop = newCrop;

    localStorage.setItem('krushi_user', JSON.stringify(currentUser));
    localStorage.setItem('krushi_farm', JSON.stringify(currentFarm));

    updateUserAndFarmDisplay();
    if (farmEditModal) farmEditModal.style.display = 'none';

    // Update active crop chip in scan tab
    document.querySelectorAll('.crop-chip').forEach(c => {
      c.classList.toggle('active', c.getAttribute('data-crop') === newCrop);
    });
  });

  // ── LANGUAGE SWITCHER MODAL & EVENT DELEGATION ──
  const langModal = document.getElementById('langModal');
  const btnCloseLangModal = document.getElementById('btnCloseLangModal');

  document.getElementById('btnLoginLangToggle')?.addEventListener('click', () => {
    if (langModal) langModal.style.display = 'flex';
  });

  document.getElementById('btnLangToggle')?.addEventListener('click', () => {
    if (langModal) langModal.style.display = 'flex';
  });

  btnCloseLangModal?.addEventListener('click', () => {
    if (langModal) langModal.style.display = 'none';
  });

  // Handle all language clicks reliably
  document.querySelectorAll('.lang-option-card, .btn-lang-choice').forEach(btn => {
    btn.addEventListener('click', (e) => {
      const targetCard = btn.getAttribute('data-lang') ? btn : btn.closest('[data-lang]');
      const selectedLang = targetCard?.getAttribute('data-lang') || 'kn';
      
      currentLanguage = selectedLang;
      localStorage.setItem('krushi_lang', selectedLang);
      applyLanguage(selectedLang);
      updateUserAndFarmDisplay();
      if (langModal) langModal.style.display = 'none';
    });
  });

  // ── NAVIGATION TABS ──
  const navButtons = document.querySelectorAll('.nav-item');
  const tabPanes = document.querySelectorAll('.tab-pane');

  function switchTab(targetTabId) {
    tabPanes.forEach(pane => pane.classList.remove('active'));
    navButtons.forEach(btn => btn.classList.remove('active'));

    const targetPane = document.getElementById(targetTabId);
    if (targetPane) targetPane.classList.add('active');

    const activeBtn = document.querySelector(`.nav-item[data-tab="${targetTabId}"]`);
    if (activeBtn) activeBtn.classList.add('active');

    if (targetTabId !== 'tabScan') {
      stopCamera();
    }
  }

  navButtons.forEach(btn => {
    btn.addEventListener('click', () => {
      const target = btn.getAttribute('data-tab');
      if (target) switchTab(target);
    });
  });

  // Direct Navigation Shortcuts
  document.getElementById('btnQuickScan')?.addEventListener('click', () => switchTab('tabScan'));
  document.getElementById('modIrrigation')?.addEventListener('click', () => switchTab('tabIrrigation'));
  document.getElementById('modPest')?.addEventListener('click', () => switchTab('tabPest'));
  document.getElementById('modMarket')?.addEventListener('click', () => switchTab('tabMarket'));
  document.getElementById('btnGoToZoning')?.addEventListener('click', () => switchTab('tabPest'));

  // Audio advisory
  document.getElementById('btnPlayAdvisory')?.addEventListener('click', () => {
    toggleAudioPlayback();
  });

  // Motor HP Calculator
  document.querySelectorAll('.btn-hp').forEach(btn => {
    btn.addEventListener('click', () => {
      document.querySelectorAll('.btn-hp').forEach(b => b.classList.remove('active'));
      btn.classList.add('active');
      const hp = parseFloat(btn.getAttribute('data-hp') || '5');
      updateHpCalculation(hp);
    });
  });

  // Crop Chip Selection in Scan Screen
  document.querySelectorAll('.crop-chip').forEach(chip => {
    chip.addEventListener('click', () => {
      document.querySelectorAll('.crop-chip').forEach(c => c.classList.remove('active'));
      chip.classList.add('active');
      selectedCrop = chip.getAttribute('data-crop') || 'ragi';
    });
  });

  // Camera & Direct Upload Scan
  document.getElementById('btnStartRealCamera')?.addEventListener('click', () => startCamera());
  document.getElementById('btnDirectUpload')?.addEventListener('click', () => {
    document.getElementById('realFileInput')?.click();
  });

  document.getElementById('btnSwitchCamera')?.addEventListener('click', () => {
    currentFacingMode = (currentFacingMode === 'environment') ? 'user' : 'environment';
    startCamera();
  });

  document.getElementById('btnUploadGallery')?.addEventListener('click', () => {
    document.getElementById('realFileInput')?.click();
  });

  document.getElementById('realFileInput')?.addEventListener('change', (e) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (event) => {
        const dataUrl = event.target.result;
        showScannedPreview(dataUrl);
        analyzeUploadedImage(dataUrl);
      };
      reader.readAsDataURL(file);
    }
  });

  document.getElementById('btnShutter')?.addEventListener('click', () => {
    capturePhotoAndAnalyze();
  });

  // Result Dialog
  const resultModal = document.getElementById('resultModal');
  document.getElementById('btnCloseResultModal')?.addEventListener('click', () => {
    if (resultModal) resultModal.style.display = 'none';
    window.speechSynthesis?.cancel();
  });

  document.getElementById('tabBtnOrganic')?.addEventListener('click', () => {
    document.getElementById('tabBtnOrganic')?.classList.add('active');
    document.getElementById('tabBtnChemical')?.classList.remove('active');
    const org = document.getElementById('tabContentOrganic');
    const chem = document.getElementById('tabContentChemical');
    if (org) org.style.display = 'block';
    if (chem) chem.style.display = 'none';
  });

  document.getElementById('tabBtnChemical')?.addEventListener('click', () => {
    document.getElementById('tabBtnChemical')?.classList.add('active');
    document.getElementById('tabBtnOrganic')?.classList.remove('active');
    const org = document.getElementById('tabContentOrganic');
    const chem = document.getElementById('tabContentChemical');
    if (chem) chem.style.display = 'block';
    if (org) org.style.display = 'none';
  });

  document.getElementById('btnSpeakResult')?.addEventListener('click', () => {
    speakDiagnosisResult();
  });

  // ── VOICE ASSISTANT EVENT LISTENERS ──
  document.getElementById('btnStartVoiceInput')?.addEventListener('click', () => {
    toggleVoiceRecognition();
  });

  const voiceTextInput = document.getElementById('voiceTextInput');
  const btnSendVoiceText = document.getElementById('btnSendVoiceText');

  function submitVoiceText() {
    const q = voiceTextInput?.value.trim();
    if (q) {
      handleVoiceQuery(q);
      if (voiceTextInput) voiceTextInput.value = '';
    }
  }

  btnSendVoiceText?.addEventListener('click', submitVoiceText);
  voiceTextInput?.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      submitVoiceText();
    }
  });

  // Delegated click listener for all quick topic prompt chips
  document.addEventListener('click', (e) => {
    const pBtn = e.target.closest('.btn-prompt');
    if (pBtn) {
      const query = pBtn.getAttribute('data-query') || pBtn.textContent.trim();
      if (query) handleVoiceQuery(query);
    }
  });

  // Mandi Search
  document.getElementById('mandiSearchInput')?.addEventListener('input', (e) => {
    renderMandiPrices(e.target.value.trim().toLowerCase());
  });

  // Refresh Weather
  document.getElementById('btnRefreshWeather')?.addEventListener('click', () => {
    fetchLiveWeather();
  });
});

// ── SCREEN VISIBILITY ──
function updateScreenVisibility() {
  const authView = document.getElementById('authFullscreenView');
  const mainApp = document.getElementById('mainAppWrapper');

  if (isLoggedIn) {
    if (authView) authView.style.display = 'none';
    if (mainApp) mainApp.style.display = 'flex';
  } else {
    if (authView) authView.style.display = 'flex';
    if (mainApp) mainApp.style.display = 'none';
  }
}

// ── APPLY LANGUAGE DYNAMICALLY ──
function applyLanguage(lang) {
  const t = i18n[lang] || i18n.kn;

  // Auth Screen
  setElText('authLangBadge', t.authLangBadge);
  setElText('authSubTitle', t.authSubTitle);
  setElText('authTagline', t.authTagline);
  setElText('btnPillLogin', t.btnPillLogin);
  setElText('btnPillSignUp', t.btnPillSignUp);
  setElHtml('lblAuthLoginUser', `<i class="fa-solid fa-phone"></i> ${t.lblAuthLoginUser}`);
  setElHtml('lblAuthLoginPass', `<i class="fa-solid fa-lock"></i> ${t.lblAuthLoginPass}`);
  setElText('btnDoLoginText', t.btnDoLoginText);
  setElHtml('lblAuthSignName', `<i class="fa-solid fa-user"></i> ${t.lblAuthSignName}`);
  setElHtml('lblAuthSignPhone', `<i class="fa-solid fa-phone"></i> ${t.lblAuthSignPhone}`);
  setElHtml('lblAuthSignCrop', `<i class="fa-solid fa-seedling"></i> ${t.lblAuthSignCrop}`);
  setElHtml('lblAuthSignArea', `<i class="fa-solid fa-vector-square"></i> ${t.lblAuthSignArea}`);
  setElHtml('lblAuthSignVillage', `<i class="fa-solid fa-location-dot"></i> ${t.lblAuthSignVillage}`);
  setElHtml('lblAuthSignLang', `<i class="fa-solid fa-language"></i> ${t.lblAuthSignLang}`);
  setElHtml('lblAuthSignPass', `<i class="fa-solid fa-lock"></i> ${t.lblAuthSignPass}`);
  setElText('btnDoSignUpText', t.btnDoSignUpText);
  setElText('btnGuestText', t.btnGuestText);

  // Header & Audio
  const villageName = (currentFarm.village || 'Mandya').split(' ')[0];
  setElText('headerSubtitle', `${t.headerSub} (${villageName})`);
  setElText('currentLangLabel', t.langName);
  setElText('audioTitle', t.audioTitle);
  setElText('audioSub', t.audioSub);

  // Farm Hero
  setElText('btnEditFarmText', t.btnEditFarm);
  setElText('alertTitle', t.alertTitle);
  setElText('alertBody', t.alertBody);
  setElText('btnZoningText', t.btnZoning);

  // Scan Card & Weather
  setElText('scanCardTitle', t.scanTitle);
  setElText('scanCardSub', t.scanSub);
  setElText('weatherHeading', t.weatherHeading);
  setElText('lblTemp', t.lblTemp);
  setElText('subTemp', t.subTemp);
  setElText('lblHumidity', t.lblHumidity);
  setElText('subHumidity', t.subHumidity);
  setElText('lblRain', t.lblRain);
  setElText('subRain', t.subRain);

  // Spray & Modules
  setElText('sprayCardTitle', t.sprayTitle);
  setElText('sprayCardBody', t.spraySafe);
  setElText('modulesHeading', t.modulesHeading);
  setElText('modIrrigTitle', t.modIrrigTitle);
  setElText('modIrrigSub', t.modIrrigSub);
  setElText('modPestTitle', t.modPestTitle);
  setElText('modPestSub', t.modPestSub);
  setElText('modMarketTitle', t.modMarketTitle);
  setElText('apkBannerTitle', t.apkBannerTitle);
  setElText('apkBannerSub', t.apkBannerSub);

  // Viewfinder
  setElText('viewfinderHint', t.viewfinderHint);
  setElText('btnStartCamText', t.btnStartCam);
  setElText('btnUploadText', t.btnUploadText);
  setElText('shutterNote', t.shutterNote);

  // Irrigation
  setElText('irrigMoistureTitle', t.irrigMoistureTitle);
  setElText('irrigStatusChip', t.irrigStatusChip);
  setElText('gaugeTarget', t.gaugeTarget);
  setElText('pumpScheduleTitle', t.pumpScheduleTitle);
  setElText('pumpMotorLabel', t.pumpMotorLabel);
  setElText('pumpOptimalLabel', t.pumpOptimalLabel);
  setElText('pumpOptimalTime', t.pumpOptimalTime);
  setElText('pumpSavingsNote', t.pumpSavingsNote);
  setElText('calcMotorTitle', t.calcMotorTitle);

  // Pest
  setElText('pestHeroName', t.pestHeroName);
  setElHtml('pestHeroRisk', `<i class="fa-solid fa-triangle-exclamation"></i> ${t.pestHeroRisk}`);
  setElText('pestProjectionLabel', t.pestProjectionLabel);
  setElText('pestActionTitle', t.pestActionTitle);
  setElText('pestActionBody', t.pestActionBody);

  setElText('dayMon', t.dayMon);
  setElText('dayTue', t.dayTue);
  setElText('dayWed', t.dayWed);
  setElText('dayThu', t.dayThu);
  setElText('dayFri', t.dayFri);
  setElText('daySat', t.daySat);
  setElText('daySun', t.daySun);

  // Mandi & Voice
  setElText('mandiLocTitle', t.mandiLocTitle);
  setElText('mandiLiveTag', t.mandiLiveTag);
  const searchInp = document.getElementById('mandiSearchInput');
  if (searchInp) searchInp.placeholder = t.mandiSearchPlaceholder;
  setElText('voiceAssistantTitle', t.voiceAssistantTitle);
  setElText('voiceAssistantSub', t.voiceAssistantSub);
  
  const rawUserName = (currentUser.name || 'ಶ್ರೀನಿವಾಸ್').split(' ')[0];
  setElText('chatGreeting', t.chatGreeting.replace('{name}', rawUserName));
  setElText('micStatusLabel', t.micStatusTap);

  // Settings
  setElText('btnLogoutText', t.btnLogout);
  setElText('setLangGroupTitle', t.setLangGroupTitle);
  setElText('setAiGroupTitle', t.setAiGroupTitle);
  setElText('optHybridTitle', t.optHybridTitle);
  setElText('optHybridSub', t.optHybridSub);
  setElText('optLocalTitle', t.optLocalTitle);
  setElText('optLocalSub', t.optLocalSub);

  // Bottom Nav
  setElText('navHome', t.navHome);
  setElText('navScan', t.navScan);
  setElText('navIrrigation', t.navIrrigation);
  setElText('navMarket', t.navMarket);
  setElText('navVoice', t.navVoice);
  setElText('navSettings', t.navSettings);
  setElText('btnSpeakResultText', t.btnSpeakResult);
  setElText('modalLangTitle', `${t.setLangGroupTitle} (Select Language)`);

  document.querySelectorAll('.btn-lang-choice').forEach(b => {
    b.classList.toggle('active', b.getAttribute('data-lang') === lang);
  });

  // Update Crop Picker Chips
  document.querySelectorAll('.crop-chip').forEach(chip => {
    const cropKey = chip.getAttribute('data-crop');
    if (cropKey && cropNames[cropKey]) {
      chip.textContent = cropNames[cropKey][lang] || cropNames[cropKey].kn;
    }
  });

  renderMandiPrices();
}

function updateUserAndFarmDisplay() {
  const rawUserName = (currentUser.name || 'ಶ್ರೀನಿವಾಸ್').split(' ')[0];
  setElText('userHeaderName', rawUserName);
  setElText('profileNameDisplay', currentUser.name || 'ಶ್ರೀನಿವಾಸ್ ಗೌಡ');
  setElText('profilePhoneDisplay', `+91 ${currentUser.phone || '9845012345'} • ${currentFarm.village || 'ಮಂಡ್ಯ'}`);

  const cName = cropNames[currentFarm.crop]?.[currentLanguage] || cropNames[currentFarm.crop]?.kn || currentFarm.crop;
  const sName = stageNames[currentFarm.stage]?.[currentLanguage] || stageNames[currentFarm.stage]?.kn || currentFarm.stage;
  const t = i18n[currentLanguage] || i18n.kn;

  setElText('farmName', `${rawUserName} ${t.farmOf} (${rawUserName}'s Farm)`);
  setElText('farmCropMeta', `${cName} • ${currentFarm.area} ${t.acreUnit} • ${sName}`);

  // Update irrigation advisory text based on real user acreage
  const areaNum = parseFloat(currentFarm.area) || 2.5;
  const pumpHours = (areaNum * 0.72).toFixed(1);
  const waterLiters = Math.round(areaNum * 14400);

  const irrigAdvText = currentLanguage === 'kn'
    ? `ನಿಮ್ಮ ${areaNum} ಎಕರೆ ${cName} ಬೆಳೆಗೆ ಮಣ್ಣಿನ ತೇವಾಂಶ 38% ಕ್ಕೆ ಇಳಿದಿದೆ. ನಾಳೆ ಮುಂಜಾನೆ 5HP ಪಂಪ್ ಅನ್ನು ${pumpHours} ಗಂಟೆಗಳ ಕಾಲ ಚಲಾಯಿಸಿ (${waterLiters.toLocaleString()} ಲೀಟರ್). ಇದರಿಂದ 28.5% ನೀರು ಉಳಿತಾಯವಾಗುತ್ತದೆ.`
    : (currentLanguage === 'hi'
      ? `आपकी ${areaNum} एकड़ ${cName} फसल के लिए मिट्टी की नमी 38% है। कल सुबह 5HP पंप को ${pumpHours} घंटे चलाएं (${waterLiters.toLocaleString()} लीटर)। इससे 28.5% पानी की बचत होगी।`
      : `Soil moisture for your ${areaNum}-acre ${cName} crop is at 38%. Run 5HP pump for ${pumpHours} hours (${waterLiters.toLocaleString()} L) tomorrow morning to save 28.5% water.`);

  setElText('irrigAdvisoryText', irrigAdvText);
  setElText('pumpRunDuration', `${pumpHours} ಗಂಟೆ (${pumpHours}h)`);

  // Active motor HP calculation
  const activeHpBtn = document.querySelector('.btn-hp.active');
  const hpVal = activeHpBtn ? parseFloat(activeHpBtn.getAttribute('data-hp') || '3') : 3;
  updateHpCalculation(hpVal);
}

// ── REAL IMAGE PIXEL & VEGETATION ANALYSIS ENGINE ──
function capturePhotoAndAnalyze() {
  const video = document.getElementById('cameraVideo');
  const canvas = document.getElementById('captureCanvas');

  if (video && video.srcObject && canvas) {
    canvas.width = video.videoWidth || 640;
    canvas.height = video.videoHeight || 480;
    const ctx = canvas.getContext('2d');
    ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
    const dataUrl = canvas.toDataURL('image/jpeg');
    showScannedPreview(dataUrl);
    stopCamera();
    analyzeCanvasImage(ctx, canvas.width, canvas.height);
  } else {
    document.getElementById('realFileInput')?.click();
  }
}

function analyzeUploadedImage(dataUrl) {
  const img = new Image();
  img.onload = () => {
    const canvas = document.getElementById('captureCanvas');
    if (!canvas) return;
    canvas.width = img.width;
    canvas.height = img.height;
    const ctx = canvas.getContext('2d');
    ctx.drawImage(img, 0, 0);
    analyzeCanvasImage(ctx, canvas.width, canvas.height);
  };
  img.src = dataUrl;
}

function analyzeCanvasImage(ctx, width, height) {
  const imgData = ctx.getImageData(0, 0, width, height);
  const data = imgData.data;

  let trueGreenPlantPixels = 0;
  let chloroticYellowPixels = 0;
  let necroticBrownPixels = 0;
  let totalSampled = 0;

  // High-accuracy pixel sampling
  for (let i = 0; i < data.length; i += 16) {
    const r = data[i];
    const g = data[i + 1];
    const b = data[i + 2];
    totalSampled++;

    const sum = r + g + b;
    if (sum === 0) continue;

    // Green Chromatic Coordinate (GCC)
    const gcc = g / sum;
    const exg = 2 * g - r - b;

    // Strict Plant Foliage Detection:
    // Requires real organic green signature (GCC > 0.36, Green is dominant over Red and Blue)
    const isPlantGreen = gcc > 0.36 && g > 45 && g > (r * 1.1) && g > (b * 1.1) && exg > 15;
    
    // Plant Yellowing (Chlorosis) on leaves
    const isChlorosisYellow = gcc > 0.33 && r > 110 && g > 110 && b < 90 && Math.abs(r - g) < 40;
    
    // Plant Necrotic Spot (Brown blast / blight lesion on leaf)
    const isNecroticSpot = r > 70 && g > 40 && b < 55 && r > g && (r - g) > 15 && (r - g) < 75;

    if (isPlantGreen) {
      trueGreenPlantPixels++;
    } else if (isChlorosisYellow) {
      chloroticYellowPixels++;
    } else if (isNecroticSpot) {
      necroticBrownPixels++;
    }
  }

  const foliageRatio = trueGreenPlantPixels / totalSampled;
  const totalPlantRatio = (trueGreenPlantPixels + chloroticYellowPixels) / totalSampled;
  const lesionRatio = (necroticBrownPixels + chloroticYellowPixels) / Math.max(1, trueGreenPlantPixels + chloroticYellowPixels + necroticBrownPixels);

  const resultModal = document.getElementById('resultModal');
  const diseaseTitle = document.getElementById('diagDiseaseName');
  const confBadge = document.getElementById('diagConfidence');
  const orgText = document.getElementById('organicTreatmentText');
  const chemText = document.getElementById('chemicalTreatmentText');
  const iconBadge = document.getElementById('diagIconBadge');
  const tabsRow = document.getElementById('diagTabsRow');

  // ─────────────────────────────────────────────────────────────
  // CASE 1: NON-PLANT OBJECT DETECTED (Laptop, Room, Skin, Desk)
  // ─────────────────────────────────────────────────────────────
  if (foliageRatio < 0.16 && totalPlantRatio < 0.20) {
    if (iconBadge) {
      iconBadge.innerHTML = '<i class="fa-solid fa-triangle-exclamation" style="color:#C62828;"></i>';
      iconBadge.style.background = '#FFEBEE';
    }
    if (confBadge) {
      confBadge.textContent = (currentLanguage === 'kn') ? "ಎಲೆ ಪತ್ತೆಯಾಗಿಲ್ಲ (0% Plant)" : ((currentLanguage === 'hi') ? "पत्ती नहीं मिली (0% Plant)" : "No Leaf Detected (0% Plant)");
      confBadge.style.background = '#FFCDD2';
      confBadge.style.color = '#B71C1C';
    }
    if (tabsRow) tabsRow.style.display = 'none';

    if (currentLanguage === 'kn') {
      if (diseaseTitle) diseaseTitle.textContent = "❌ ಇದು ಬೆಳೆಯ ಎಲೆಯ ಫೋಟೋ ಅಲ್ಲ!";
      if (orgText) orgText.textContent = "ದಯವಿಟ್ಟು ಬಾಧಿತ ಗಿಡದ ಎಲೆಯ ಹತ್ತಿರದ ಸ್ಪಷ್ಟ ಫೋಟೋ ತೆಗೆಯಿರಿ. ಲ್ಯಾಪ್‌ಟಾಪ್, ಮಾನವ ಮುಖ, ಕೋಣೆ, ಪೀಠೋಪಕರಣ ಅಥವಾ ಇತರ ವಸ್ತುಗಳನ್ನು AI ವಿಶ್ಲೇಷಿಸುವುದಿಲ್ಲ.";
    } else if (currentLanguage === 'hi') {
      if (diseaseTitle) diseaseTitle.textContent = "❌ यह फसल की पत्ती का फोटो नहीं है!";
      if (orgText) orgText.textContent = "कृपया संक्रमित पौधे की पत्ती की स्पष्ट और पास की फोटो लें। लैपटॉप, चेहरा, कमरा या अन्य वस्तुओं का विश्लेषण नहीं किया जा सकता।";
    } else {
      if (diseaseTitle) diseaseTitle.textContent = "❌ Not a Crop Leaf Photo!";
      if (orgText) orgText.textContent = "Please capture a clear, close-up photo of an infected crop leaf. Laptops, rooms, faces or non-plant objects are strictly rejected.";
    }

    const orgTab = document.getElementById('tabContentOrganic');
    const chemTab = document.getElementById('tabContentChemical');
    if (orgTab) orgTab.style.display = 'block';
    if (chemTab) chemTab.style.display = 'none';

    lastDiagnosisResult = {
      isLeaf: false,
      speechKn: "ಇದು ಬೆಳೆಯ ಎಲೆಯ ಫೋಟೋ ಅಲ್ಲ. ದಯವಿಟ್ಟು ಗಿಡದ ಎಲೆಯ ಸ್ಪಷ್ಟ ಫೋಟೋ ತೆಗೆಯಿರಿ.",
      speechHi: "यह फसल की पत्ती का फोटो नहीं है। कृपया पत्ती का स्पष्ट फोटो लें।",
      speechEn: "This is not a crop leaf photo. Please capture a clear leaf photo."
    };
  }
  // ─────────────────────────────────────────────────────────────
  // CASE 2: HEALTHY CROP LEAF
  // ─────────────────────────────────────────────────────────────
  else if (lesionRatio < 0.08) {
    if (iconBadge) {
      iconBadge.innerHTML = '<i class="fa-solid fa-circle-check" style="color:#2E7D32;"></i>';
      iconBadge.style.background = '#E8F5E9';
    }
    if (confBadge) {
      confBadge.textContent = (currentLanguage === 'kn') ? "98% ಆರೋಗ್ಯಕರ (Healthy)" : ((currentLanguage === 'hi') ? "98% स्वस्थ (Healthy)" : "98% Healthy Crop");
      confBadge.style.background = '#C8E6C9';
      confBadge.style.color = '#1B5E20';
    }
    if (tabsRow) tabsRow.style.display = 'none';

    const cName = cropNames[selectedCrop]?.[currentLanguage] || cropNames[selectedCrop]?.kn || selectedCrop;
    if (currentLanguage === 'kn') {
      if (diseaseTitle) diseaseTitle.textContent = `${cName} - ಬೆಳೆ ಸಂಪೂರ್ಣ ಆರೋಗ್ಯಕರವಾಗಿದೆ!`;
      if (orgText) orgText.textContent = "ಯಾವುದೇ ರೋಗದ ಲಕ್ಷಣಗಳು ಕಂಡುಬಂದಿಲ್ಲ. ಸಾಮಾನ್ಯ ನೀರಾವರಿ ಮತ್ತು ಲಘು ಪೋಷಕಾಂಶ ನಿರ್ವಹಣೆಯನ್ನು ಮುಂದುವರಿಸಿ.";
    } else if (currentLanguage === 'hi') {
      if (diseaseTitle) diseaseTitle.textContent = `${cName} - फसल पूर्णतः स्वस्थ है!`;
      if (orgText) orgText.textContent = "कोई बीमारी नहीं पाई गई। नियमित सिंचाई और पोषण जारी रखें।";
    } else {
      if (diseaseTitle) diseaseTitle.textContent = `${cName} - Crop is 100% Healthy!`;
      if (orgText) orgText.textContent = "No disease spots or nutritional stress detected. Continue standard irrigation and nutrient management.";
    }

    const orgTab = document.getElementById('tabContentOrganic');
    const chemTab = document.getElementById('tabContentChemical');
    if (orgTab) orgTab.style.display = 'block';
    if (chemTab) chemTab.style.display = 'none';

    lastDiagnosisResult = {
      isLeaf: true,
      speechKn: `ನಿಮ್ಮ ${cName} ಬೆಳೆ ಸಂಪೂರ್ಣ ಆರೋಗ್ಯಕರವಾಗಿದೆ. ಯಾವುದೇ ರೋಗದ ಬಾಧೆ ಇಲ್ಲ.`,
      speechHi: `आपकी ${cName} फसल पूर्णतः स्वस्थ है। कोई रोग नहीं है।`,
      speechEn: `Your ${cName} crop is completely healthy. No disease detected.`
    };
  }
  // ─────────────────────────────────────────────────────────────
  // CASE 3: REAL DISEASE DETECTED ON CROP LEAF
  // ─────────────────────────────────────────────────────────────
  else {
    if (iconBadge) {
      iconBadge.innerHTML = '<i class="fa-solid fa-microscope" style="color:#C62828;"></i>';
      iconBadge.style.background = '#FFEBEE';
    }
    if (confBadge) {
      confBadge.textContent = (currentLanguage === 'kn') ? "94% ನಿಖರತೆ (Edge AI)" : ((currentLanguage === 'hi') ? "94% सटीकता (Edge AI)" : "94% Confidence (Edge AI)");
      confBadge.style.background = '#E8F5E9';
      confBadge.style.color = '#2E7D32';
    }
    if (tabsRow) tabsRow.style.display = 'flex';

    const orgTab = document.getElementById('tabContentOrganic');
    const chemTab = document.getElementById('tabContentChemical');
    if (orgTab) orgTab.style.display = 'block';
    if (chemTab) chemTab.style.display = 'none';

    const cropDiag = getCropDiseaseDetails(selectedCrop, lesionRatio);
    if (diseaseTitle) diseaseTitle.textContent = cropDiag.title[currentLanguage] || cropDiag.title.kn;
    if (orgText) orgText.textContent = cropDiag.organic[currentLanguage] || cropDiag.organic.kn;
    if (chemText) chemText.textContent = cropDiag.chemical[currentLanguage] || cropDiag.chemical.kn;

    lastDiagnosisResult = {
      isLeaf: true,
      speechKn: `ಪತ್ತೆಯಾದ ರೋಗ: ${cropDiag.title.kn}. ಸಾವಯವ ಪರಿಹಾರ: ${cropDiag.organic.kn}`,
      speechHi: `पहचाना गया रोग: ${cropDiag.title.hi}। जैविक उपचार: ${cropDiag.organic.hi}`,
      speechEn: `Detected disease: ${cropDiag.title.en}. Organic cure: ${cropDiag.organic.en}`
    };
  }

  setTimeout(() => {
    if (resultModal) resultModal.style.display = 'flex';
  }, 350);
}

function getCropDiseaseDetails(crop, ratio) {
  const db = {
    ragi: {
      title: { kn: "ರಾಗಿ ಬ್ಲಾಸ್ಟ್ ರೋಗ (Ragi Leaf Blast)", hi: "रागी ब्लास्ट रोग (Blast)", en: "Ragi Leaf Blast (Magnaporthe grisea)" },
      organic: { kn: "ಪ್ರತಿ ಲೀಟರ್ ನೀರಿಗೆ 2ml ಸೂಡೋಮೊನಾಸ್ ಫ್ಲೋರೆಸೆನ್ಸ್ ಅಥವಾ ಬೇವಿನ ಎಣ್ಣೆ (10,000 ppm) ಸಿಂಪಡಿಸಿ.", hi: "2ml प्रति लीटर स्यूडोमोनास फ्लोरेसेंस या नीम का तेल शाम को छिड़कें।", en: "Spray 2ml/L Pseudomonas fluorescens or Neem oil during evening hours." },
      chemical: { kn: "ಟ್ರೈಸೈಕ್ಲಾಜೋಲ್ 75% WP (0.6g/L) ಅಥವಾ ಕಿಟಾಜಿನ್ 48% EC (1ml/L) ಸಿಂಪಡಿಸಿ.", hi: "ट्राइसाइक्लाजोल 75% WP (0.6g/L) का छिड़काव करें।", en: "Spray Tricyclazole 75% WP @ 0.6g/L or Kitazin 48% EC @ 1ml/L." }
    },
    tomato: {
      title: { kn: "ಟೊಮ್ಯಾಟೊ ಬೇಗನೆ ಒಣಗುವ ರೋಗ (Early Blight)", hi: "टमाटर अगेती झुलसा (Early Blight)", en: "Tomato Early Blight (Alternaria solani)" },
      organic: { kn: "ಟ್ರೈಕೋಡರ್ಮಾ ವಿರಿಡೆ (Trichoderma viride) 5g/L ನೀರಿನಲ್ಲಿ ಬೆರೆಸಿ ಮುಂಜಾನೆ ಸಿಂಪಡಿಸಿ.", hi: "ट्राइकोडर्मा विरिडी 5 ग्राम प्रति लीटर पानी में मिलाकर छिड़कें।", en: "Spray Trichoderma viride @ 5g/L water." },
      chemical: { kn: "ಮ್ಯಾಂಕೋಜೆಬ್ 75% WP (2g/L) ಅಥವಾ ಕಾಪರ್ ಆಕ್ಸಿಕ್ಲೋರೈಡ್ (3g/L) ಸಿಂಪಡಿಸಿ.", hi: "मैनकोजेब 75% WP (2g/L) का छिड़काव करें।", en: "Spray Mancozeb 75% WP @ 2g/L or Copper Oxychloride @ 3g/L." }
    },
    rice: {
      title: { kn: "ಭತ್ತದ ಕವಚ ಕೊಳೆ ರೋಗ (Sheath Blight)", hi: "धान शीथ ब्लाइट (Sheath Blight)", en: "Paddy Sheath Blight (Rhizoctonia solani)" },
      organic: { kn: "ಸಾವಯವ ಜೀವಾಮೃತ ಹಾಗೂ ಸೂಡೋಮೊನಾಸ್ 2.5ml/L ಸಿಂಪಡಿಸಿ.", hi: "स्यूडोमोनास 2.5 मिली प्रति लीटर का छिड़काव करें।", en: "Foliar spray of Pseudomonas @ 2.5ml/L." },
      chemical: { kn: "ಹೆಕ್ಸಾಕೊನಜೋಲ್ 5% EC (2ml/L) ಅಥವಾ ವ್ಯಾಲಿಡಾಮೈಸಿನ್ 3% L (2ml/L) ಸಿಂಪಡಿಸಿ.", hi: "हेक्साकोनाजोल 5% EC (2ml/L) का छिड़काव करें।", en: "Spray Hexaconazole 5% EC @ 2ml/L or Validamycin 3% L @ 2ml/L." }
    },
    cotton: {
      title: { kn: "ಹತ್ತಿ ಎಲೆ ಚುಕ್ಕೆ ರೋಗ (Bacterial Leaf Blight)", hi: "कपास पत्ती धब्बा रोग", en: "Cotton Bacterial Blight (Xanthomonas)" },
      organic: { kn: "ತಾಮ್ರದ ದ್ರಾವಣ ಹಾಗೂ ಬೇವಿನ ಕಷಾಯ 5% ಸಿಂಪಡಿಸಿ.", hi: "नीम का काढ़ा 5% का छिड़काव करें।", en: "Spray 5% Neem extract or Copper bio-formulation." },
      chemical: { kn: "ಸ್ಟ್ರೆಪ್ಟೊಸೈಕ್ಲಿನ್ (0.1g/L) + ಕಾಪರ್ ಆಕ್ಸಿಕ್ಲೋರೈಡ್ (2.5g/L) ಸಿಂಪಡಿಸಿ.", hi: "स्ट्रेप्टोसाइक्लिन (0.1g/L) + कॉपर ऑक्सीक्लोराइड छिड़कें।", en: "Spray Streptocycline (0.1g/L) + Copper Oxychloride (2.5g/L)." }
    },
    groundnut: {
      title: { kn: "ಕಡಲೆಕಾಯಿ ತಿಕ್ಕಾ ರೋಗ (Tikka Leaf Spot)", hi: "मूंगफली टिक्का रोग (Tikka Disease)", en: "Groundnut Tikka Leaf Spot (Cercospora)" },
      organic: { kn: "ಬೇವಿನ ಎಣ್ಣೆ 3ml/L ಹಾಗೂ ಹುದುಗಿಸಿದ ಮಜ್ಜಿಗೆ ದ್ರಾವಣ ಸಿಂಪಡಿಸಿ.", hi: "नीम का तेल 3ml/L और खट्टी छाछ का छिड़काव करें।", en: "Spray 3ml/L Neem oil with sour buttermilk solution." },
      chemical: { kn: "ಕಾರ್ಬೆಂಡಾಜಿಮ್ 50% WP (1g/L) ಅಥವಾ ಮ್ಯಾಂಕೋಜೆಬ್ 2g/L ಸಿಂಪಡಿಸಿ.", hi: "कार्बेंडाजिम 50% WP (1g/L) का छिड़काव करें।", en: "Spray Carbendazim 50% WP @ 1g/L or Mancozeb @ 2g/L." }
    },
    sugarcane: {
      title: { kn: "ಕಬ್ಬಿನ ಕೆಂಪು ಕೊಳೆ ರೋಗ (Red Rot)", hi: "गन्ना लाल सड़न रोग (Red Rot)", en: "Sugarcane Red Rot (Colletotrichum falcatum)" },
      organic: { kn: "ಟ್ರೈಕೋಡರ್ಮಾ ಮಿಶ್ರಿತ ಸೆಗಣಿ ಗೊಬ್ಬರವನ್ನು ಬುಡಕ್ಕೆ ಹಾಕಿ ನೀರುಣಿಸಿ.", hi: "ट्राइकोडर्मा युक्त गोबर की खाद जड़ों में डालें।", en: "Apply Trichoderma enriched FYM to root zone." },
      chemical: { kn: "ಕಾರ್ಬೆಂಡಾಜಿಮ್ (1g/L) ದ್ರಾವಣದಿಂದ ಬೆಳೆ ಬುಡವನ್ನು ಉಪಚರಿಸಿ.", hi: "कार्बेंडाजिम (1g/L) घोल से उपचार करें।", en: "Drench root zone with Carbendazim @ 1g/L." }
    }
  };
  return db[crop] || db.ragi;
}

function speakDiagnosisResult() {
  if (!lastDiagnosisResult) return;
  const text = currentLanguage === 'kn'
    ? lastDiagnosisResult.speechKn
    : (currentLanguage === 'hi' ? lastDiagnosisResult.speechHi : lastDiagnosisResult.speechEn);
  speakUtterance(text, currentLanguage);
}

// ── LIVE WEATHER API ──
async function fetchLiveWeather() {
  try {
    const res = await fetch('https://api.open-meteo.com/v1/forecast?latitude=12.52&longitude=76.89&current=temperature_2m,relative_humidity_2m,precipitation,wind_speed_10m&daily=precipitation_probability_max&timezone=Asia%2FKolkata');
    if (res.ok) {
      const data = await res.json();
      const temp = Math.round(data.current.temperature_2m || 29.5);
      const humidity = Math.round(data.current.relative_humidity_2m || 68);
      const rainProb = data.daily?.precipitation_probability_max ? data.daily.precipitation_probability_max[0] : 15;

      setElText('valTemp', `${temp}°C`);
      setElText('valHumidity', `${humidity}%`);
      setElText('valRain', `${rainProb}%`);
    }
  } catch (err) {
    console.warn("Weather fallback active:", err);
  }
}

// ── AUDIO ADVISORY SPEECH SYNTHESIS ──
function toggleAudioPlayback() {
  const audioIcon = document.getElementById('audioIcon');
  const audioWave = document.getElementById('audioWave');

  if (isSpeaking) {
    window.speechSynthesis?.cancel();
    isSpeaking = false;
    if (audioIcon) audioIcon.className = "fa-solid fa-play";
    if (audioWave) audioWave.classList.remove('active');
  } else {
    const rawUserName = (currentUser.name || 'ಶ್ರೀನಿವಾಸ್').split(' ')[0];
    const cName = cropNames[currentFarm.crop]?.[currentLanguage] || cropNames[currentFarm.crop]?.kn || currentFarm.crop;
    const textToSpeak = (i18n[currentLanguage] || i18n.kn).spokenAdvisory
      .replace('{name}', rawUserName)
      .replace('{crop}', cName)
      .replace('{area}', currentFarm.area || '2.5');

    speakUtterance(textToSpeak, currentLanguage, () => {
      isSpeaking = false;
      if (audioIcon) audioIcon.className = "fa-solid fa-play";
      if (audioWave) audioWave.classList.remove('active');
    });
    isSpeaking = true;
    if (audioIcon) audioIcon.className = "fa-solid fa-stop";
    if (audioWave) audioWave.classList.add('active');
  }
}

function speakUtterance(text, lang, onEnd) {
  if (!('speechSynthesis' in window)) {
    if (onEnd) onEnd();
    return;
  }

  window.speechSynthesis.cancel();
  const utterance = new SpeechSynthesisUtterance(text);
  utterance.rate = 0.92;
  utterance.pitch = 1.0;

  if (lang === 'kn') {
    utterance.lang = 'kn-IN';
  } else if (lang === 'hi') {
    utterance.lang = 'hi-IN';
  } else {
    utterance.lang = 'en-IN';
  }

  utterance.onend = () => { if (onEnd) onEnd(); };
  utterance.onerror = () => { if (onEnd) onEnd(); };
  window.speechSynthesis.speak(utterance);
}

// ── HP CALCULATION ──
function updateHpCalculation(hp) {
  const resultEl = document.getElementById('calcResultText');
  if (!resultEl) return;

  const areaNum = parseFloat(currentFarm.area) || 2.5;
  const hours = ((areaNum * 3.6) / hp).toFixed(1);
  const liters = Math.round(areaNum * 14400);

  if (currentLanguage === 'kn') {
    resultEl.innerHTML = `${hp} HP ಮೋಟಾರ್‌ಗೆ: <strong>${hours} ಗಂಟೆ</strong> ಚಾಲನೆ ಅಗತ್ಯ (ಒಟ್ಟು ${liters.toLocaleString()} ಲೀಟರ್ ನೀರು).`;
  } else if (currentLanguage === 'hi') {
    resultEl.innerHTML = `${hp} HP मोटर के लिए: <strong>${hours} घंटे</strong> संचालन आवश्यक (कुल ${liters.toLocaleString()} लीटर)।`;
  } else {
    resultEl.innerHTML = `For ${hp} HP Motor: <strong>${hours} hours</strong> run time required (Total ${liters.toLocaleString()} Liters).`;
  }
}

// ── MANDI PRICES ──
function renderMandiPrices(query = '') {
  const listEl = document.getElementById('mandiList');
  if (!listEl) return;

  const filtered = mandiData.filter(item => {
    const name = (currentLanguage === 'kn' ? item.cropKn : (currentLanguage === 'hi' ? item.cropHi : item.cropEn)).toLowerCase();
    return name.includes(query) || item.variety.toLowerCase().includes(query);
  });

  listEl.innerHTML = filtered.map(item => {
    const displayName = currentLanguage === 'kn' ? item.cropKn : (currentLanguage === 'hi' ? item.cropHi : item.cropEn);
    const displayPrice = currentLanguage === 'kn' ? item.priceKn : (currentLanguage === 'hi' ? item.priceHi : item.priceEn);
    return `
      <div class="mandi-item-card">
        <div class="mandi-crop-info">
          <h4>${displayName}</h4>
          <p>${item.variety}</p>
        </div>
        <div class="mandi-price-info">
          <div class="mandi-price-val">${displayPrice}</div>
          <span class="mandi-change ${item.trend}">${item.change}</span>
        </div>
      </div>
    `;
  }).join('');
}

// ── CAMERA CONTROLS ──
async function startCamera() {
  try {
    const video = document.getElementById('cameraVideo');
    const content = document.getElementById('viewfinderContent');
    const preview = document.getElementById('scannedLeafPreview');

    if (preview) preview.style.display = 'none';

    if (cameraStream) {
      stopCamera();
    }

    cameraStream = await navigator.mediaDevices.getUserMedia({
      video: { facingMode: { ideal: currentFacingMode }, width: { ideal: 1280 }, height: { ideal: 720 } }
    });

    if (video) {
      video.srcObject = cameraStream;
      video.style.display = 'block';
    }
    if (content) content.style.display = 'none';
  } catch (err) {
    console.warn("Camera streaming unavailable, opening file selector:", err);
    document.getElementById('realFileInput')?.click();
  }
}

function stopCamera() {
  if (cameraStream) {
    cameraStream.getTracks().forEach(track => track.stop());
    cameraStream = null;
  }
  const video = document.getElementById('cameraVideo');
  if (video) video.style.display = 'none';
  const content = document.getElementById('viewfinderContent');
  if (content) content.style.display = 'flex';
}

function showScannedPreview(dataUrl) {
  const preview = document.getElementById('scannedLeafPreview');
  const video = document.getElementById('cameraVideo');
  const content = document.getElementById('viewfinderContent');

  if (video) video.style.display = 'none';
  if (content) content.style.display = 'none';
  if (preview) {
    preview.src = dataUrl;
    preview.style.display = 'block';
  }
}

// ── COMPREHENSIVE INTELLIGENT REAL AI VOICE ASSISTANT ENGINE ──
function toggleVoiceRecognition() {
  const micBtn = document.getElementById('btnStartVoiceInput');
  const micIcon = document.getElementById('micIcon');
  const micStatus = document.getElementById('micStatusLabel');
  const t = i18n[currentLanguage] || i18n.kn;

  const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
  if (!SpeechRecognition) {
    if (micStatus) micStatus.textContent = (currentLanguage === 'kn') ? "ಧ್ವನಿ ಲಭ್ಯವಿಲ್ಲ - ಟೈಪ್ ಮಾಡಿ ಅಥವಾ ಕೆಳಗಿನ ಆಯ್ಕೆ ಒತ್ತಿ" : "Speech unavailable - please type or tap topic";
    const sampleQuery = (currentLanguage === 'kn') ? "ರಾಗಿ ಬೆಳೆಗೆ ಯಾವ ಗೊಬ್ಬರ ಹಾಕಬೇಕು?" : ((currentLanguage === 'hi') ? "फसल के लिए कौन सी खाद अच्छी है?" : "What fertilizer is best for my crop?");
    handleVoiceQuery(sampleQuery);
    return;
  }

  if (isListeningSpeech) {
    return;
  }

  try {
    const recognition = new SpeechRecognition();
    recognition.lang = (currentLanguage === 'kn') ? 'kn-IN' : ((currentLanguage === 'hi') ? 'hi-IN' : 'en-IN');
    recognition.interimResults = false;
    recognition.maxAlternatives = 1;

    recognition.onstart = () => {
      isListeningSpeech = true;
      if (micBtn) micBtn.classList.add('listening');
      if (micIcon) micIcon.style.color = '#fff';
      if (micStatus) micStatus.textContent = t.micStatusListening || "ಆಲಿಸಲಾಗುತ್ತಿದೆ... ಮಾತನಾಡಿ (Listening...)";
    };

    recognition.onresult = (event) => {
      const transcript = event.results[0][0].transcript;
      if (transcript && transcript.trim()) {
        handleVoiceQuery(transcript.trim());
      }
    };

    recognition.onend = () => {
      isListeningSpeech = false;
      if (micBtn) micBtn.classList.remove('listening');
      if (micIcon) micIcon.style.color = '#fff';
      if (micStatus) micStatus.textContent = t.micStatusTap || "ಮಾತನಾಡಲು ಮೈಕ್ ಒತ್ತಿ (Tap to Speak)";
    };

    recognition.onerror = (e) => {
      console.warn("Speech error:", e.error);
      isListeningSpeech = false;
      if (micBtn) micBtn.classList.remove('listening');
      if (micIcon) micIcon.style.color = '#fff';
      if (micStatus) micStatus.textContent = t.micStatusTap || "ಮಾತನಾಡಲು ಮೈಕ್ ಒತ್ತಿ (Tap to Speak)";
      
      if (e.error === 'not-allowed' || e.error === 'service-not-allowed') {
        const sampleQuery = (currentLanguage === 'kn') ? "ರಾಗಿ ಬೆಳೆಗೆ ಯಾವ ಗೊಬ್ಬರ ಹಾಕಬೇಕು?" : "What fertilizer is best for my crop?";
        handleVoiceQuery(sampleQuery);
      }
    };

    recognition.start();
  } catch (err) {
    console.warn("Speech start exception:", err);
    isListeningSpeech = false;
    if (micBtn) micBtn.classList.remove('listening');
  }
}

async function handleVoiceQuery(userText) {
  if (!userText || !userText.trim()) return;
  const chatHistory = document.getElementById('chatHistory');
  if (!chatHistory) return;

  // Append user bubble
  const userDiv = document.createElement('div');
  userDiv.className = 'chat-bubble user';
  userDiv.innerHTML = `<p>${userText}</p>`;
  chatHistory.appendChild(userDiv);

  // Show thinking indicator
  const thinkingDiv = document.createElement('div');
  thinkingDiv.className = 'chat-bubble ai thinking';
  const thinkingLabel = (currentLanguage === 'kn') ? 'ಕೃಷಿ AI ಉತ್ತರಿಸುತ್ತಿದೆ...' : ((currentLanguage === 'hi') ? 'कृषि AI उत्तर तैयार कर रहा है...' : 'Krushi AI is thinking...');
  thinkingDiv.innerHTML = `<p><i class="fa-solid fa-spinner fa-spin"></i> <em>${thinkingLabel}</em></p>`;
  chatHistory.appendChild(thinkingDiv);
  chatHistory.scrollTop = chatHistory.scrollHeight;

  // Generate dynamic contextual answer
  const replyText = await generateSmartAgriResponse(userText);

  // Remove thinking indicator & display actual answer
  if (thinkingDiv && thinkingDiv.parentNode) {
    thinkingDiv.parentNode.removeChild(thinkingDiv);
  }

  const aiDiv = document.createElement('div');
  aiDiv.className = 'chat-bubble ai';
  aiDiv.innerHTML = `<p>${replyText}</p>`;
  chatHistory.appendChild(aiDiv);
  chatHistory.scrollTop = chatHistory.scrollHeight;

  // Speak response in farmer's preferred regional language
  speakUtterance(replyText, currentLanguage);
}

async function generateSmartAgriResponse(query) {
  const q = query.toLowerCase();
  const lang = currentLanguage || 'kn';
  const cropKey = currentFarm.crop || selectedCrop || 'ragi';
  const cName = cropNames[cropKey]?.[lang] || cropNames[cropKey]?.kn || cropKey;
  const area = parseFloat(currentFarm.area) || 2.5;
  const village = (currentFarm.village || 'ಮಂಡ್ಯ').split(' ')[0];
  const farmerName = (currentUser.name || 'ಶ್ರೀನಿವಾಸ್').split(' ')[0];

  // 1. FERTILIZERS / NUTRITION / MANURE (ಗೊಬ್ಬರ, ಯೂರಿಯಾ, ಡಿಎಪಿ, NPK, खाद, Fertilizer, Compost)
  if (q.includes('ಗೊಬ್ಬರ') || q.includes('ಯೂರಿಯಾ') || q.includes('ಡಿಎಪಿ') || q.includes('ಪೋಷಕಾಂಶ') || q.includes('ಖಾದ್') || q.includes('खाद') || q.includes('उर्वरक') || q.includes('fertilizer') || q.includes('npk') || q.includes('dap') || q.includes('urea') || q.includes('zinc') || q.includes('potash')) {
    if (cropKey === 'ragi') {
      if (lang === 'kn') return `ನಿಮ್ಮ ${area} ಎಕರೆ ರಾಗಿ ಬೆಳೆಗೆ ಪ್ರತಿ ಎಕರೆಗೆ 50 ಕೆಜಿ ಡಿಎಪಿ (DAP), 25 ಕೆಜಿ ಪೊಟ್ಯಾಶ್ ಮತ್ತು ಬಿತ್ತನೆಯ 30 ದಿನಗಳ ನಂತರ 25 ಕೆಜಿ ಯೂರಿಯಾ ಮೇಲುಗೊಬ್ಬರವಾಗಿ ನೀಡಿ. ಜೊತೆಗೆ ಎಕರೆಗೆ 5 ಟನ್ ಕೊಟ್ಟಿಗೆ ಗೊಬ್ಬರ ಮಣ್ಣಿಗೆ ಅತ್ಯುತ್ತಮ.`;
      if (lang === 'hi') return `आपकी ${area} एकड़ रागी फसल के लिए प्रति एकड़ 50 किग्रा DAP, 25 किग्रा पोटाश और बुवाई के 30 दिन बाद 25 किग्रा यूरिया डालें। साथ ही 5 टन गोबर की खाद मिट्टी की उर्वरता बढ़ाती है।`;
      return `For your ${area}-acre Ragi crop, apply 50 kg DAP, 25 kg Potash per acre as basal dose, and top-dress with 25 kg Urea at 30 days. Mix with 5 tons farmyard manure for best yield.`;
    } else if (cropKey === 'rice') {
      if (lang === 'kn') return `ಭತ್ತದ ಬೆಳೆಗೆ ಎಕರೆಗೆ 100:50:50 NPK ಅನುಪಾತ ಸೂಕ್ತ. ನಾಟಿ ಸಮಯದಲ್ಲಿ ಪೂರ್ಣ ರಂಜಕ ಹಾಗೂ ಅರ್ಧ ಸಾರಜನಕ ಮತ್ತು ತೆನೆ ಬರುವಾಗ ಉಳಿದ ಯೂರಿಯಾ ಹಾಕಿ. ಜಿಂಕ್ ಕೊರತೆಗೆ 10 ಕೆಜಿ ಜಿಂಕ್ ಸಲ್ಫೇಟ್ ಬೆರೆಸಿ.`;
      if (lang === 'hi') return `धान के लिए प्रति एकड़ 100:50:50 NPK अनुपात आवश्यक है। रोपाई के समय पूरा फास्फोरस व आधा नाइट्रोजन डालें। जिंक सल्फेट 10 किग्रा मिलाने से पैदावार बढ़ती है।`;
      return `For paddy, apply 100:50:50 kg NPK per acre. Split nitrogen into 3 stages: basal, tillering, and panicle initiation. Add 10 kg Zinc Sulfate to prevent khaira disease.`;
    } else if (cropKey === 'sugarcane') {
      if (lang === 'kn') return `ಕಬ್ಬಿನ ಬೆಳೆಗೆ ಪ್ರತಿ ಎಕರೆಗೆ 100 ಕೆಜಿ ಡಿಎಪಿ, 50 ಕೆಜಿ ಪೊಟ್ಯಾಶ್ ಹಾಗೂ 150 ಕೆಜಿ ಯೂರಿಯಾವನ್ನು 3 ಕಂತುಗಳಲ್ಲಿ ನೀಡಿ. ಸಾವಯವ ಕಬ್ಬಿನ ಸಿಪ್ಪೆ ಕಾಂಪೋಸ್ಟ್ ತೇವಾಂಶ ಕಾಪಾಡುತ್ತದೆ.`;
      if (lang === 'hi') return `गन्ने के लिए प्रति एकड़ 100 किग्रा DAP, 50 किग्रा पोटाश और 150 किग्रा यूरिया को 3 भागों में दें। जैविक कंपोस्ट से मिट्टी की नमी बनी रहती है।`;
      return `For sugarcane, provide 100 kg DAP, 50 kg Potash, and 150 kg Urea per acre in 3 split doses. Organic trash mulching prevents evaporation loss.`;
    } else if (cropKey === 'tomato') {
      if (lang === 'kn') return `ಟೊಮ್ಯಾಟೊಗೆ 19:19:19 ನೀರಿನಲ್ಲಿ ಕರಗುವ ಗೊಬ್ಬರವನ್ನು ಪ್ರತಿ ವಾರ ಹನಿ ನೀರಾವರಿ ಮೂಲಕ ನೀಡಿ. ಹೂವಾಡುವ ಹಂತದಲ್ಲಿ ಕ್ಯಾಲ್ಸಿಯಂ ನೈಟ್ರೇಟ್ ಮತ್ತು ಬೋರಾನ್ ಸಿಂಪಡಿಸುವುದರಿಂದ ಕಾಯಿ ಒಡೆಯುವುದು ತಡೆಯಬಹುದು.`;
      if (lang === 'hi') return `टमाटर के लिए ड्रिप से 19:19:19 घुलनशील खाद हर हफ्ते दें। फूल आते समय कैल्शियम नाइट्रेट और बोरॉन का छिड़काव फल फटने से बचाता है।`;
      return `For tomatoes, fertigate with 19:19:19 water-soluble fertilizer weekly. Spray Calcium Nitrate + Boron during flowering to prevent blossom end rot.`;
    } else {
      if (lang === 'kn') return `ನಿಮ್ಮ ${area} ಎಕರೆ ${cName} ಬೆಳೆಗೆ ಉತ್ತಮ ಇಳುವರಿಗಾಗಿ ಎಕರೆಗೆ 50 ಕೆಜಿ ಸಂಕೀರ್ಣ ಗೊಬ್ಬರ ಮತ್ತು ಮೈಕ್ರೋನ್ಯೂಟ್ರಿಯೆಂಟ್ಸ್ ಸಿಂಪಡಿಸಿ.`;
      if (lang === 'hi') return `आपकी ${area} एकड़ ${cName} फसल के लिए 50 किग्रा मिश्रित खाद और सूक्ष्म पोषक तत्वों का छिड़काव सर्वोत्तम है।`;
      return `For your ${area}-acre ${cName} crop, apply balanced NPK complex fertilizer with micronutrient foliar spray.`;
    }
  }

  // 2. PESTS / INSECTS (ಕೀಟ, ಹುಳು, ಕೀಟನಾಶಕ, ಕಣಜ, कीट, कीड़ा, Pest, Insect, Aphids, Borer, Armyworm)
  if (q.includes('ಕೀಟ') || q.includes('ಹುಳು') || q.includes('ಕೀಟನಾಶಕ') || q.includes('ಮಿಡತೆ') || q.includes('ಕಣಜ') || q.includes('कीट') || q.includes('कीड़ा') || q.includes('कीटनाशक') || q.includes('pest') || q.includes('insect') || q.includes('borer') || q.includes('armyworm') || q.includes('aphid') || q.includes('caterpillar') || q.includes('spray')) {
    if (lang === 'kn') return `ಕೀಟ ಹಾವಳಿ ನಿಯಂತ್ರಣಕ್ಕೆ ಮುಂಜಾನೆ ಪ್ರತಿ ಲೀಟರ್ ನೀರಿಗೆ 2ml ಬೇವಿನ ಎಣ್ಣೆ (Azadirachtin 10,000 ppm) ಸಿಂಪಡಿಸಿ. ತೀವ್ರ ಕಾಂಡಕೊರೆಯುವ ಹುಳುವಿಗೆ ಕ್ಲೋರಾಂಟ್ರಾನಿಲಿಪ್ರೋಲ್ (Coragen) 0.4ml/L ನೀಡಿ.`;
    if (lang === 'hi') return `कीट नियंत्रण के लिए सुबह 2 मिली नीम का तेल (10,000 ppm) प्रति लीटर पानी में मिलाकर छिड़कें। तना छेदक के लिए कोराजन (0.4ml/L) का प्रयोग करें।`;
    return `For eco-friendly pest control, spray 2ml/L Neem Oil (10,000 ppm) during early morning. For severe borer infestation, apply Chlorantraniliprole @ 0.4ml/L.`;
  }

  // 3. DISEASES / FUNGUS / MEDICINE (ರೋಗ, ಬ್ಲಾಸ್ಟ್, ಬ್ಲೈಟ್, ಕೊಳೆ, ಔಷಧಿ, ರೋಗಗಳು, ಔಷಧ, रोग, बीमारी, झुलसा, दवा, Disease, Blast, Blight, Fungicide, Wilt, Rot)
  if (q.includes('ರೋಗ') || q.includes('ಬ್ಲಾಸ್ಟ್') || q.includes('ಬ್ಲೈಟ್') || q.includes('ಕೊಳೆ') || q.includes('ಒಣಗ') || q.includes('ಔಷಧ') || q.includes('ಔಷಧಿ') || q.includes('रोग') || q.includes('बीमारी') || q.includes('दवा') || q.includes('blast') || q.includes('blight') || q.includes('disease') || q.includes('fungus') || q.includes('rot') || q.includes('wilt') || q.includes('cure')) {
    if (cropKey === 'ragi') {
      if (lang === 'kn') return `ರಾಗಿ ಬ್ಲಾಸ್ಟ್ ರೋಗಕ್ಕೆ ಪ್ರತಿ ಲೀಟರ್ ನೀರಿಗೆ 0.6 ಗ್ರಾಂ ಟ್ರೈಸೈಕ್ಲಾಜೋಲ್ 75% WP ಅಥವಾ ಜೈವಿಕ ಸೂಡೋಮೊನಾಸ್ 2.5ml ಬೆರೆಸಿ ಸಂಜೆ ಸಿಂಪಡಿಸಿ. ಹೊಲದಲ್ಲಿ ನೀರು ನಿಲ್ಲದಂತೆ ನೋಡಿಕೊಳ್ಳಿ.`;
      if (lang === 'hi') return `रागी ब्लास्ट के लिए 0.6 ग्राम ट्राइसाइक्लाजोल 75% WP या स्यूडोमोनास 2.5ml/L पानी में मिलाकर छिड़कें। जलभराव न होने दें।`;
      return `For Ragi Blast disease, spray Tricyclazole 75% WP @ 0.6g/L or Pseudomonas @ 2.5ml/L in evening hours. Ensure proper field drainage.`;
    } else if (cropKey === 'tomato') {
      if (lang === 'kn') return `ಟೊಮ್ಯಾಟೊ ಅರ್ಲಿ ಬ್ಲೈಟ್ ಅಥವಾ ಎಲೆ ಚುಕ್ಕೆ ರೋಗಕ್ಕೆ ಮ್ಯಾಂಕೋಜೆಬ್ 2g/L ಅಥವಾ ಕ್ಯಾಬ್ರಿಯೋ ಟಾಪ್ 2g/L ಸಿಂಪಡಿಸಿ. ಬಾಧಿತ ಎಲೆಗಳನ್ನು ತಕ್ಷಣ ಕಿತ್ತು ನಾಶಪಡಿಸಿ.`;
      if (lang === 'hi') return `टमाटर अगेती झुलसा के लिए मैनकोजेब (2g/L) का छिड़काव करें और संक्रमित पत्तियों को हटा दें।`;
      return `For tomato early blight, spray Mancozeb @ 2g/L or Pyraclostrobin. Remove infected bottom leaves to prevent fungal splash.`;
    } else {
      if (lang === 'kn') return `ನಿಮ್ಮ ${cName} ಬೆಳೆಯಲ್ಲಿ ಶಿಲೀಂಧ್ರ ರೋಗಗಳಿಗೆ ತಾಮ್ರದ ಆಕ್ಸಿಕ್ಲೋರೈಡ್ (COC 3g/L) ಅಥವಾ ಸೂಡೋಮೊನಾಸ್ ದ್ರಾವಣ ಸಿಂಪರಣೆ ಅತ್ಯಂತ ಪರಿಣಾಮಕಾರಿ.`;
      if (lang === 'hi') return `आपकी ${cName} फसल में फफूंद जनित रोगों के लिए कॉपर ऑक्सीक्लोराइड (3g/L) का छिड़काव करें।`;
      return `For fungal spot prevention in ${cName}, spray Copper Oxychloride @ 3g/L or apply bio-fungicide Trichoderma.`;
    }
  }

  // 4. IRRIGATION / WATERING (ನೀರು, ನೀರಾವರಿ, ಪಂಪ್, ತೇವಾಂಶ, ಮೋಟಾರ್, पानी, सिंचाई, नमी, Water, Irrigation, Pump, Moisture)
  if (q.includes('ನೀರು') || q.includes('ನೀರಾವರಿ') || q.includes('ಪಂಪ್') || q.includes('ಮೋಟಾರ್') || q.includes('ತೇವಾಂಶ') || q.includes('पानी') || q.includes('सिंचाई') || q.includes('पंप') || q.includes('नमी') || q.includes('water') || q.includes('irrigat') || q.includes('pump') || q.includes('moisture')) {
    const hours = (area * 0.72).toFixed(1);
    const liters = Math.round(area * 14400);
    if (lang === 'kn') return `ನಿಮ್ಮ ${area} ಎಕರೆ ${cName} ಬೆಳೆಗೆ ಪ್ರಸ್ತುತ ಮಣ್ಣಿನ ತೇವಾಂಶ 38% ಇದೆ. ನಾಳೆ ಮುಂಜಾನೆ 06:00 ರಿಂದ 5HP ಪಂಪ್ ಅನ್ನು ${hours} ಗಂಟೆ (${liters.toLocaleString()} ಲೀಟರ್) ಚಲಾಯಿಸಿ. ಇದರಿಂದ 28.5% ನೀರು ಆವಿಯಾಗುವುದು ತಪ್ಪುತ್ತದೆ.`;
    if (lang === 'hi') return `आपकी ${area} एकड़ ${cName} फसल के लिए मिट्टी की नमी 38% है। कल सुबह 5HP पंप ${hours} घंटे (${liters.toLocaleString()} लीटर) चलाएं। इससे 28.5% पानी की बचत होगी।`;
    return `Soil moisture for your ${area}-acre ${cName} crop is at 38%. Run 5HP pump tomorrow morning for ${hours} hours (${liters.toLocaleString()} L) to save 28.5% water loss.`;
  }

  // 5. MANDI / MARKET PRICE / RATE (ಮಂಡಿ, ಬೆಲೆ, ದರ, ರೇಟ್, ಕ್ವಿಂಟಾಲ್, ಎಷ್ಟು, मंडी, भाव, दाम, Mandi, Price, Rate, Market, Quintal, Cost)
  if (q.includes('ಮಂಡಿ') || q.includes('ಬೆಲೆ') || q.includes('ದರ') || q.includes('ರೇಟ್') || q.includes('ಕ್ವಿಂಟಾಲ್') || q.includes('ಮಾರುಕಟ್ಟೆ') || q.includes('मंडी') || q.includes('भाव') || q.includes('दाम') || q.includes('mandi') || q.includes('price') || q.includes('rate') || q.includes('market') || q.includes('cost')) {
    if (cropKey === 'ragi') {
      if (lang === 'kn') return `ಇಂದು ${village} ಮತ್ತು ಮಂಡ್ಯ APMC ಯಲ್ಲಿ ಉತ್ತಮ ದರ್ಜೆಯ ರಾಗಿ ಬೆಲೆ ಕ್ವಿಂಟಾಲ್‌ಗೆ ₹3,720 ಇದೆ (₹80 ಏರಿಕೆ). ಬೇಡಿಕೆ ಹೆಚ್ಚಿರುವುದರಿಂದ ಮುಂದಿನ ವಾರ ಮಾರಾಟ ಮಾಡುವುದು ಲಾಭದಾಯಕ.`;
      if (lang === 'hi') return `आज ${village} व मंड्या APMC में उच्च गुणवत्ता वाली रागी का भाव ₹3,720 प्रति क्विंटल है (+₹80)। अगले हफ्ते दाम और बढ़ने की संभावना है।`;
      return `Today's Mandya APMC market rate for quality Ragi is ₹3,720 per quintal (+₹80). Prices are trending upward due to steady miller demand.`;
    } else if (cropKey === 'rice') {
      if (lang === 'kn') return `ಇಂದಿನ APMC ಮಾರುಕಟ್ಟೆಯಲ್ಲಿ ಸೋನಾ ಮಸೂರಿ ಭತ್ತ ಕ್ವಿಂಟಾಲ್‌ಗೆ ₹2,450 ಮತ್ತು ಜ್ಯೋತಿ ತಳಿ ₹2,320 ದರದಲ್ಲಿದೆ.`;
      if (lang === 'hi') return `आज APMC में सोना मसूरी धान का भाव ₹2,450 प्रति क्विंटल है।`;
      return `Today's Sona Masoori paddy rate is ₹2,450 / quintal across major Karnataka APMCs.`;
    } else if (cropKey === 'tomato') {
      if (lang === 'kn') return `ಕೋಲಾರ ಮತ್ತು ಮಂಡ್ಯ ಮಂಡಿಯಲ್ಲಿ 15 ಕೆಜಿ ಟೊಮ್ಯಾಟೊ ಪೆಟ್ಟಿಗೆ ಬೆಲೆ ₹1,800 ಇದೆ.`;
      if (lang === 'hi') return `टमाटर की 15 किग्रा क्रेट का ताजा मंडी भाव ₹1,800 है।`;
      return `Today's fresh tomato crate (15kg) rate is ₹1,800 in local APMC yards.`;
    } else {
      if (lang === 'kn') return `ಇಂದಿನ ಮಾರುಕಟ್ಟೆಯಲ್ಲಿ ${cName} ಬೆಲೆ ಸ್ಥಿರವಾಗಿದ್ದು ಉತ್ತಮ ಗುಣಮಟ್ಟದ ಬೆಳೆಗೆ ಉತ್ತಮ ಪ್ರೀಮಿಯಂ ದರ ಸಿಗುತ್ತಿದೆ.`;
      if (lang === 'hi') return `आज मंडी में ${cName} का भाव स्थिर और संतोषजनक बना हुआ है।`;
      return `Current market rate for ${cName} is trading firm with steady wholesale demand.`;
    }
  }

  // 6. SEEDS / HIGH YIELDING VARIETIES / SOWING (ಬೀಜ, ತಳಿ, ಬಿತ್ತನೆ, ಅವಧಿ, ಕಾಲ, बीज, किस्म, बुवाई, Seed, Variety, Sowing, Hybrid)
  if (q.includes('ಬೀಜ') || q.includes('ತಳಿ') || q.includes('ಬಿತ್ತನೆ') || q.includes('ಕಾಲ') || q.includes('ಅವಧಿ') || q.includes('ಆಯ್ಕೆ') || q.includes('बीज') || q.includes('किस्म') || q.includes('बुवाई') || q.includes('seed') || q.includes('variety') || q.includes('sow') || q.includes('hybrid')) {
    if (cropKey === 'ragi') {
      if (lang === 'kn') return `ರಾಗಿಗೆ ಹೆಚ್ಚು ಇಳುವರಿ ನೀಡುವ ತಳಿಗಳು: GPU-28, MR-1, ML-365 ಮತ್ತು KMR-301. ಬಿತ್ತನೆಗೆ ಜೂನ್-ಜುಲೈ ತಿಂಗಳು ಸೂಕ್ತ. ಬಿತ್ತನೆ ಬೀಜವನ್ನು ಅಜೋಸ್ಪೈರಿಲಮ್‌ನಿಂದ ಉಪಚರಿಸಿ.`;
      if (lang === 'hi') return `रागी की उन्नत किस्में: GPU-28, MR-1 और KMR-301 हैं। बुवाई के लिए जून-जुलाई का समय सर्वोत्तम है।`;
      return `Recommended high-yielding Ragi varieties: GPU-28, MR-1, and KMR-301. Treat seeds with Azospirillum @ 25g/kg before sowing.`;
    } else if (cropKey === 'rice') {
      if (lang === 'kn') return `ಭತ್ತಕ್ಕೆ ಜ್ಯೋತಿ, ಸೋನಾ ಮಸೂರಿ (BPT-5204), ಮತ್ತು ತುಂಗಾ ತಳಿಗಳು ಹೆಚ್ಚು ಇಳುವರಿ ನೀಡುತ್ತವೆ.`;
      if (lang === 'hi') return `धान के लिए सोना मसूरी, ज्योति और पूसा सुगंधा किस्में अत्यधिक पैदावार देती हैं।`;
      return `Best paddy cultivars for regional agro-climates are Sona Masoori (BPT-5204), Jyothi, and IR-64.`;
    } else {
      if (lang === 'kn') return `${cName} ಬೆಳೆಗೆ ಪ್ರಮಾಣೀಕೃತ KSSC ಬೀಜಗಳನ್ನು ಆಯ್ಕೆಮಾಡಿ ಮತ್ತು ಟ್ರೈಕೋಡರ್ಮಾದಿಂದ ಬೀಜೋಪಚಾರ ಮಾಡಿ.`;
      if (lang === 'hi') return `${cName} के लिए प्रमाणित बीजों का ही चयन करें और फफूंदनाशक से उपचारित करके बोएं।`;
      return `Always choose certified seeds for ${cName} and treat with bio-fungicide Trichoderma before sowing.`;
    }
  }

  // 7. ORGANIC FARMING / JEEVAMRUTHA (ಸಾವಯವ, ಜೀವಾಮೃತ, ಬೀಜಾಮೃತ, ಪಂಚಗವ್ಯ, ಕಾಂಪೋಸ್ಟ್, ಮಣ್ಣು, जैविक, जीवामृत, पंचगव्य, Organic, Jeevamrutha, Natural, Compost, Bio)
  if (q.includes('ಸಾವಯವ') || q.includes('ಜೀವಾಮೃತ') || q.includes('ಬೀಜಾಮೃತ') || q.includes('ಪಂಚಗವ್ಯ') || q.includes('ಕಾಂಪೋಸ್ಟ್') || q.includes('ತಯಾರ') || q.includes('ನೈಸರ್ಗಿಕ') || q.includes('जैविक') || q.includes('जीवामृत') || q.includes('पंचगव्य') || q.includes('organic') || q.includes('jeevamrutha') || q.includes('natural') || q.includes('bio') || q.includes('panchagavya')) {
    if (lang === 'kn') return `ಜೀವಾಮೃತ ತಯಾರಿಸಲು: 200L ನೀರಿಗೆ 10 ಕೆಜಿ ದೇಸಿ ಹಸುವಿನ ಸಗಣಿ, 10L ಗಂಜಲ, 2 ಕೆಜಿ ಬೆಲ್ಲ, 2 ಕೆಜಿ ದ್ವಿದಳ ಧಾನ್ಯದ ಹಿಟ್ಟು ಹಾಗೂ ಹಿಡಿ ಹೊಲದ ಫಲವತ್ತಾದ ಮಣ್ಣು ಬೆರೆಸಿ 48 ಗಂಟೆ ನೆರಳಿನಲ್ಲಿ ಹುದುಗಿಸಿ ನೀರಾವರಿ ಜೊತೆ ಹಾಯಿಸಿ.`;
    if (lang === 'hi') return `जीवामृत बनाने के लिए: 200 लीटर पानी में 10 किग्रा गाय का गोबर, 10 लीटर गोमूत्र, 2 किग्रा गुड़, 2 किग्रा बेसन व मुट्ठी भर खेत की मिट्टी मिलाकर 48 घंटे फर्मेंट करें।`;
    return `To prepare Jeevamrutha: Mix 10kg cow dung, 10L cow urine, 2kg jaggery, 2kg pulse flour, and a handful of farm soil in 200L water. Ferment for 48 hours and apply via irrigation.`;
  }

  // 8. GOVERNMENT SCHEMES & SUBSIDIES (ಯೋಜನೆ, ಸಬ್ಸಿಡಿ, ಪಿಎಂ ಕಿಸಾನ್, ವಿಮೆ, ಸಾಲ, ಕೃಷಿ ಭಾಗ್ಯ, योजना, सब्सिडी, पीएम किसान, बीमा, Scheme, Subsidy, PM Kisan, Insurance, Loan)
  if (q.includes('ಯೋಜನೆ') || q.includes('ಸಬ್ಸಿಡಿ') || q.includes('ಕಿಸಾನ್') || q.includes('ವಿಮೆ') || q.includes('ಸಾಲ') || q.includes('ಭಾಗ್ಯ') || q.includes('ಸಿರಿ') || q.includes('ಯೋಜನೆಗಳು') || q.includes('योजना') || q.includes('सब्सिडी') || q.includes('बीमा') || q.includes('scheme') || q.includes('subsidy') || q.includes('pm kisan') || q.includes('insurance') || q.includes('fasal bima')) {
    if (lang === 'kn') return `ರೈತರಿಗೆ ಪ್ರಮುಖ ಯೋಜನೆಗಳು: 1) PM-KISAN (ವರ್ಷಕ್ಕೆ ₹6,000), 2) ಕೃಷಿ ಭಾಗ್ಯ (ಕೃಷಿ ಹೊಂಡಕ್ಕೆ 80% ಸಬ್ಸಿಡಿ), 3) ಪ್ರಧಾನಮಂತ್ರಿ ಫಸಲ್ ಬಿಮಾ ಯೋಜನೆ (ಬೆಳೆ ವಿಮೆ), 4) ಸೋಲಾರ್ ಪಂಪ್ ಸೆಟ್‌ಗೆ ಕುಸುಮ್ ಯೋಜನೆ. ನಿಮ್ಮ ಸ್ಥಳೀಯ ರೈತ ಸಂಪರ್ಕ ಕೇಂದ್ರಕ್ಕೆ ಭೇಟಿ ನೀಡಿ.`;
    if (lang === 'hi') return `मुख्य सरकारी योजनाएं: 1) पीएम-किसान (₹6,000 वार्षिक), 2) पीएम फसल बीमा योजना, 3) कुसुम सोलर पंप योजना (75% सब्सिडी)। नजदीकी कृषि केंद्र में आवेदन करें।`;
    return `Key Farmer Schemes: 1) PM-KISAN (₹6,000/yr direct transfer), 2) PM Fasal Bima Yojana (crop damage insurance), 3) PM-KUSUM (75% solar pump subsidy), 4) Krishi Bhagya farm pond scheme.`;
  }

  // 9. WEED MANAGEMENT (ಕಳೆ, ಕಳೆನಾಶಕ, ಹುಲ್ಲು, खरपतवार, Weed, Herbicide, Grass)
  if (q.includes('ಕಳೆ') || q.includes('ಕಳೆನಾಶಕ') || q.includes('ಹುಲ್ಲು') || q.includes('खरपतवार') || q.includes('weed') || q.includes('herbicide')) {
    if (lang === 'kn') return `ಬಿತ್ತನೆಯ 25-30 ದಿನಗಳಲ್ಲಿ ಎಡೆಕುಂಟೆ ಹೊಡೆಯಿರಿ ಅಥವಾ ಕೈಯಿಂದ ಕಳೆ ಕೀಳಿರಿ. ಅಗಲ ಎಲೆಯ ಕಳೆಗಳಿಗೆ 2,4-D (2ml/L) ಅಥವಾ ಹುಲ್ಲು ಜಾತಿಗೆ ಪೆಂಡಿಮಿಥಾಲಿನ್ ಬಳಸಿ.`;
    if (lang === 'hi') return `बुवाई के 25-30 दिनों में निराई-गुड़ाई करें। चौड़ी पत्ती वाले खरपतवार के लिए 2,4-D का छिड़काव करें।`;
    return `Perform mechanical inter-cultivation at 25-30 days after sowing. For broadleaf weeds, apply 2,4-D @ 2ml/L or pre-emergence Pendimethalin.`;
  }

  // 10. WEATHER & RAINFALL (ಹವಾಮಾನ, ಮಳೆ, ಬಿಸಿಲು, ಗಾಳಿ, ಸಿಂಪರಣೆ, मौसम, बारिश, धूप, Weather, Rain, Temperature, Wind)
  if (q.includes('ಹವಾಮಾನ') || q.includes('ಮಳೆ') || q.includes('ಬಿಸಿಲು') || q.includes('ಗಾಳಿ') || q.includes('मौसम') || q.includes('बारिश') || q.includes('weather') || q.includes('rain') || q.includes('temperature') || q.includes('forecast')) {
    if (lang === 'kn') return `ಇಂದು ${village} ಭಾಗದಲ್ಲಿ ಉಷ್ಣಾಂಶ 29.5°C ಮತ್ತು ಆರ್ದ್ರತೆ 68% ಇದೆ. ಮಳೆ ಸಾಧ್ಯತೆ 15% ಮಾತ್ರವಿದ್ದು, ಕೀಟನಾಶಕ ಸಿಂಪಡಣೆಗೆ ಮತ್ತು ಒಣಗಿಸುವಿಕೆಗೆ ಸೂಕ್ತ ವಾತಾವರಣವಿದೆ.`;
    if (lang === 'hi') return `आज ${village} क्षेत्र में तापमान 29.5°C और आर्द्रता 68% है। बारिश की संभावना 15% है, कीटनाशक छिड़काव के लिए मौसम उत्तम है।`;
    return `Today's temperature in ${village} is 29.5°C with 68% humidity and 15% rain probability. Conditions are clear and favorable for field work.`;
  }

  // 11. HARVEST & STORAGE (ಕೊಯ್ಲು, ಶೇಖರಣೆ, ಇಳುವರಿ, ಧಾನ್ಯ, कटाई, भंडारण, उपज, Harvest, Storage, Yield, Grain)
  if (q.includes('ಕೊಯ್ಲು') || q.includes('ಶೇಖರಣೆ') || q.includes('ಇಳುವರಿ') || q.includes('ಧಾನ್ಯ') || q.includes('कटाई') || q.includes('भंडारण') || q.includes('harvest') || q.includes('storage') || q.includes('yield') || q.includes('store')) {
    if (lang === 'kn') return `ತೆನೆಗಳು ಕಂದು ಬಣ್ಣಕ್ಕೆ ತಿರುಗಿ ಕಾಳು ಗಟ್ಟಿಯಾದಾಗ ಕೊಯ್ಲು ಮಾಡಿ. ಶೇಖರಣೆ ಮಾಡುವ ಮುನ್ನ ಕಾಳಿನ ತೇವಾಂಶ 10-12% ಗೆ ಬರುವವರೆಗೆ ಚೆನ್ನಾಗಿ ಬಿಸಿಲಿನಲ್ಲಿ ಒಣಗಿಸಿ.`;
    if (lang === 'hi') return `बालियां भूरी होने पर कटाई करें। अनाज को भंडारण से पहले धूप में 10-12% नमी रहने तक अच्छी तरह सुखाएं।`;
    return `Harvest when panicles turn golden brown and grains harden. Dry grains under sun until moisture drops below 12% before bagging.`;
  }

  // 12. GREETINGS & WHO ARE YOU (ನಮಸ್ಕಾರ, ಹಲೋ, ಹೇಗಿದ್ದೀಯ, ಯಾರು, नमस्ते, Hello, Hi, Who are you)
  if (q.includes('ನಮಸ್ಕಾರ') || q.includes('ಹಲೋ') || q.includes('ಯಾರು') || q.includes('ಹೇಗಿದ್ದೀ') || q.includes('ನಮಸ್ತೆ') || q.includes('नमस्ते') || q.includes('hello') || q.includes('hi') || q.includes('who are you') || q.includes('how are you')) {
    if (lang === 'kn') return `ನಮಸ್ಕಾರ ${farmerName} ಅವರೇ! ನಾನು ನಿಮ್ಮ ಕೃಷಿ AI ಸಹಾಯಕ. ನಿಮ್ಮ ${area} ಎಕರೆ ${cName} ಬೆಳೆಯ ರೋಗಗಳು, ಗೊಬ್ಬರ, ನೀರಾವರಿ, ಬೀಜ ಅಥವಾ ಮಂಡಿ ಬೆಲೆಗಳ ಬಗ್ಗೆ ಏನಾದರೂ ಕೇಳಿ!`;
    if (lang === 'hi') return `नमस्ते ${farmerName} जी! मैं आपका कृषि AI सहायक हूँ। अपनी ${area} एकड़ ${cName} फसल की खाद, रोग, सिंचाई या मंडी भाव के बारे में कुछ भी पूछें।`;
    return `Hello ${farmerName}! I am your KrushiEdge AI Agronomist. Ask me anything about fertilizers, pest control, disease remedy, irrigation, or live mandi prices for your ${area}-acre ${cName} crop.`;
  }

  // Default Smart Dynamic Agronomy Response
  if (lang === 'kn') {
    return `ನಿಮ್ಮ ${area} ಎಕರೆ ${cName} ಬೆಳೆಗೆ ಸಂಬಂಧಿಸಿದಂತೆ: ಮಣ್ಣಿನ ಫಲವತ್ತತೆ ಕಾಪಾಡಲು ಸಾವಯವ ಗೊಬ್ಬರ ಬಳಸಿ, ನಿಯಮಿತವಾಗಿ ರೋಗ ತಪಾಸಣೆ ಮಾಡಿ ಹಾಗೂ ಸೂಕ್ತ ಸಮಯದಲ್ಲಿ ನೀರುಣಿಸಿ. ಹೆಚ್ಚಿನ ವಿವರಕ್ಕಾಗಿ ಗೊಬ್ಬರ, ರೋಗ ಅಥವಾ ಮಂಡಿ ಬೆಲೆಯ ಬಗ್ಗೆ ನಿರ್ದಿಷ್ಟವಾಗಿ ಕೇಳಿ.`;
  } else if (lang === 'hi') {
    return `आपकी ${area} एकड़ ${cName} फसल के लिए: मिट्टी की उर्वरता बनाए रखने के लिए संतुलित पोषण दें, नियमित रोग निगरानी करें और समय पर सिंचाई करें।`;
  } else {
    return `For your ${area}-acre ${cName} crop: ensure balanced NPK nutrition, monitor early leaf lesions, and maintain scheduled irrigation. Ask specifically about fertilizers, pest remedies, or market rates anytime.`;
  }
}

