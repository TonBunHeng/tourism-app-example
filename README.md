# Smart Tourism App (Android Native)

Native Android mobile application for **AngkorVers / Smart Tourism Cambodia**, engineered to achieve complete feature parity with the **tourism-travel** web portal and powered by the **tourism-backend-api** Laravel 13 REST API.

---

## 🏛️ Architecture & Tech Stack

- **Platform:** Native Android (Minimum SDK 24, Target SDK 34)
- **Language:** Java
- **Architecture Pattern:** MVVM (`Activity` / `Fragment` ➔ `ViewModel` (LiveData) ➔ `Repository` ➔ `ApiService`)
- **Networking:** Retrofit 2 + OkHttp 3 with Bearer Auth Interceptor, auto-logout on `401 Unauthorized`, and custom Laravel validation error parser (`ApiErrorParser`)
- **Local Storage:** `EncryptedSharedPreferences` via Android Jetpack Security (`MasterKey` AES256-GCM) with standard `SharedPreferences` fallback
- **UI & Layouts:** Material Design 3 (Material Components), View Binding, XML layouts, Card-based responsive grids, Shimmer / Progress indicators, Swipe-to-Refresh (`SwipeRefreshLayout`)
- **Image Loading:** Glide (Disk caching, center-crop, circular avatar crop)
- **Localization:** Dynamic runtime bilingual support (English `values/` and Khmer `values-km/`)
- **Theme:** Dynamic Light and Dark Theme support (`AppCompatDelegate DayNight`)

---

## 🚀 Running the App

### Prerequisites
1. **Java JDK 17+** (or bundled Android Studio JBR: `/Applications/Android Studio.app/Contents/jbr/Contents/Home`)
2. **Android SDK** with platform tools (`adb`)
3. **Android Emulator** (API 28+ recommended with Google Play Services)
4. **Backend API** running locally on Docker (`http://localhost:8000`) or remote server

### Build & Install
```bash
# Set Java environment (macOS with Android Studio)
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"

# Clean and compile debug APK
./gradlew assembleDebug

# Install on running emulator or connected device
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Backend Base URL Configuration
The API endpoint is configured in `app/build.gradle` via `BuildConfig.BASE_URL`:
- **Debug Build:** `http://10.0.2.2:8000/api/travel/` (Android Emulator loopback to host Docker backend)
- **Release Build:** `https://angkorvers.vercel.app/api/travel/`
- **Dynamic Override:** Configurable at runtime through `AppPreference.setCustomBaseUrl(...)`.

---

## 📱 Implemented Traveler Features (Parity with Web)

### 1. Authentication & Session Management
- **Sign In:** Email and password with field-level Laravel validation error displays (`SignInActivity`).
- **Sign Up:** Role selection (Tourist / Traveler vs Business Owner), province dropdown, bio, phone, and password confirmation (`RegisterActivity`).
- **Google Sign-In:** Integrated Google Sign-In SDK (`GoogleSignInClient`).
- **Remember Me:** Encrypted credential caching for seamless sign-in across app restarts.
- **Forgot Password:** Helper dialog and support routing.
- **Session Expiry Handling:** Automatic 401 interceptor clearing session and routing to sign-in.
- **Sign Out:** Token revocation with server-side `POST /api/travel/auth/logout`.

### 2. Home Screen (`HomeFragment`)
- **Hero Banner:** Angkor & Beyond discovery header with in-banner search bar.
- **Quick Discovery Badges:** 1-tap shortcuts to Businesses, Provinces, Bookings, and AI Assistant.
- **Categories Carousel:** Horizontal list displaying all travel categories.
- **Provinces Carousel:** Horizontal list of Cambodian provinces with place count badges.
- **Featured Destinations:** Horizontal card list of top featured attractions.
- **Popular Attractions:** Vertical list with ratings, reviews count, and wishlist hearts.
- **Emergency Hotlines:** 1-tap direct dialers for Police (117), Ambulance (119), and Fire (118).

### 3. Destinations & Explore (`ExploreFragment`)
- **Full Directory:** Paginated list of places with image previews, category tags, and province labels.
- **Keyword Search:** Real-time query search against place title and description.
- **Category Filter:** Filter destinations by category chip selection.
- **Province Filter:** Filter destinations by Cambodian province.
- **Sorting Options:** Sort by Popularity, Rating, or Newest.
- **Rating Filter:** Minimum rating slider / filter.
- **Swipe-to-Refresh & Empty States:** Clean error retry and empty state graphics.

### 4. Destination Detail (`PlaceDetailActivity`)
- **Hero Image & Title:** High-resolution header with favorite heart toggle.
- **Tour Info Chips:** Best visiting time, estimated duration, and entry price.
- **Location & Map Navigation:** Address, coordinates, and 1-tap "Get Directions" opening Google Maps.
- **Reviews & Ratings:** Full reviews list with reviewer avatars, star rating, and timestamps.
- **Write Review:** Dialog to rate (1–5 stars) and submit traveler reviews (`POST /api/travel/reviews`).
- **Book Tour / Visit:** Reservation dialog with date picker, time slot, guest counter, and special requests.
- **System Share:** Native Android share intent for destination links.

### 5. Businesses & Directory (`BusinessesActivity`, `BusinessDetailActivity`)
- **Directory Grid:** Hotels, resorts, restaurants, and tour operators.
- **Business Details:** Contact phone, email, website, verified status badge, and location.
- **Services Offered:** Business service offerings with pricing and duration.
- **Direct Service Booking:** Dialog to book specific services directly.
- **Operating Hours:** Weekly schedule and opening times.
- **Business Reviews:** Traveler ratings and feedback.

### 6. Bookings Management (`BookingsFragment`, `BookingDetailActivity`)
- **Status Filter:** Filter bookings by `All`, `Pending`, `Confirmed`, `Completed`, or `Cancelled`.
- **Booking Cards:** Reference number, target place / service, date/time, and colored status chips.
- **Ticket / Receipt Detail Screen:** Comprehensive booking receipt.
- **Cancellation Flow:** In-app cancellation with optional reason prompt (`POST /api/travel/bookings/{id}/cancel`).

### 7. Saved Favorites / Wishlist (`FavoritesFragment`)
- **Wishlist Grid:** Saved destinations with image, title, rating, and province.
- **Filtering:** Filter by `All`, `Visited`, or `Wishlist`.
- **Toggle Visited Status:** Mark places as visited with checkmark toggle (`PATCH /api/travel/favorites/{id}/toggle-visited`).
- **Remove from Wishlist:** Instant removal with server sync (`DELETE /api/travel/favorites/{id}`).

### 8. Notifications (`NotificationsActivity`)
- **Notification Feed:** Real-time updates for booking confirmations, trip reminders, and alerts.
- **Unread Badge:** Live counter for unread notifications.
- **Read Actions:** Tap notification to mark read; "Mark all as read" button.
- **Delete:** Individual notification deletion.

### 9. Emergency & Support (`SupportActivity`)
- **Emergency Hotlines:** 1-tap dialer for Police (117), Fire (118), Ambulance (119), and Tourist Police (+855 23 724 793).
- **FAQs Accordion:** Frequently asked questions on bookings, temple rules, and Angkor passes.
- **Feedback Form:** Send inquiries and feedback directly to the tourism support team (`POST /api/travel/contact`).

### 10. AngkorVers AI Assistant (`AIChatActivity`)
- **Conversational Travel Companion:** AI-powered travel guide for Cambodian heritage and itineraries.
- **Prompt Suggestions:** Horizontal suggestion chips (e.g., "Plan 3-day Siem Reap trip", "Angkor Wat dress code").
- **Real-time Chat:** Interactive chat bubbles with loading/thinking states (`POST /api/travel/ai/chat`).

### 11. Profile & Settings (`ProfileFragment`)
- **Profile Overview:** Avatar, full name, email, traveler/business role badge, location, and bio.
- **Avatar Upload:** Camera / Gallery file picker with multipart image upload (`POST /api/travel/auth/avatar`).
- **Edit Profile:** Modal dialog to update name, phone, province, and travel bio (`PUT /api/travel/auth/profile`).
- **Change Password:** Secure update with current password validation (`PUT /api/travel/auth/password`).
- **Localization:** Bilingual switch between English and Khmer (ភាសាខ្មែរ) with runtime locale recreation.
- **Dark Theme:** Light and Dark mode toggle with instant theme switching.
- **Privacy Erasure:** GDPR-compliant account deletion request (`POST /api/travel/deletion-requests`).
- **Sign Out:** Complete session clearing and routing to sign-in.

---

## 📁 Project Structure

```
smarttourismapp/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/example/smarttourismapp/
│   │   │   ├── TourismApplication.java       # App init (dark mode, locale)
│   │   │   ├── data/
│   │   │   │   ├── local/                    # Database & Preferences
│   │   │   │   ├── remote/                   # Retrofit, ApiClient, ApiService, ErrorParser, Resource
│   │   │   │   └── repository/               # AuthRepository, TravelRepository
│   │   │   ├── model/                        # User, Place, Business, Booking, Category, Province, etc.
│   │   │   ├── ui/
│   │   │   │   ├── ai/                       # AIChatActivity, ChatAdapter, ChatMessage
│   │   │   │   ├── auth/                     # SignInActivity, RegisterActivity, AuthViewModel
│   │   │   │   ├── booking/                  # BookingsFragment, BookingDetailActivity, BookingAdapter
│   │   │   │   ├── business/                 # BusinessesActivity, BusinessDetailActivity, BusinessAdapter
│   │   │   │   ├── category/                 # CategoriesActivity
│   │   │   │   ├── favorite/                 # FavoritesFragment, FavoritesAdapter
│   │   │   │   ├── notification/             # NotificationsActivity, NotificationAdapter
│   │   │   │   ├── province/                 # ProvincesActivity
│   │   │   │   ├── splash/                   # SplashActivity
│   │   │   │   ├── support/                  # SupportActivity
│   │   │   │   └── traveller/                # TravellerMainActivity, HomeFragment, ExploreFragment, PlaceDetailActivity, ProfileFragment
│   │   │   └── utils/                        # AppPreference, LocaleHelper, Constants
│   │   └── res/
│   │       ├── layout/                       # Complete XML layouts & dialogs
│   │       ├── values/                       # English strings, colors, themes, styles
│   │       ├── values-km/                    # Khmer strings
│   │       └── values-night/                 # Dark theme colors & styles
│   └── build.gradle                          # Dependencies, BuildConfig fields
└── docs/
    ├── PARITY.md                             # 60/60 Web vs Android Parity Matrix
    └── TESTING.md                            # Feature-by-feature manual test plan
```

---

## ⚠️ Known Limitations & Notes

1. **Google Sign-In on Emulator:** Requires the Android Emulator image to include **Google Play Services** (e.g., Pixel 5 with Google Play icon).
2. **Cleartext Traffic:** Enabled (`android:usesCleartextTraffic="true"`) for local testing against Docker `http://10.0.2.2:8000`. Production builds targeting HTTPS endpoints (`https://angkorvers.vercel.app`) enforce TLS by default.
3. **Dialer on Emulator:** Tapping emergency numbers opens the Android dialer with the number pre-filled; actual call placement requires a physical SIM or carrier network.
