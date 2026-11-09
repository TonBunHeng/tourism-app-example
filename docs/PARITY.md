# Tourism Travel App & Web Parity Matrix

This document tracks feature parity between the **tourism-travel** web portal, the **tourism-backend-api** Laravel REST API, and the **smarttourismapp** Android application.

## Parity Table

| # | Feature | Website Page / Component | API Endpoint | Android Screen / Component | Status |
|---|---------|--------------------------|--------------|----------------------------|--------|
| 1 | Auth: Sign In | `src/pages/auth/Login.jsx` | `POST /api/travel/auth/login` | `ui/auth/SignInActivity` | Done |
| 2 | Auth: Sign Up (Tourist & Business Owner) | `src/pages/auth/Register.jsx` | `POST /api/travel/auth/register` | `ui/auth/RegisterActivity` | Done |
| 3 | Auth: Google Sign-In | `src/components/common/GoogleButton.jsx` | `POST /api/travel/auth/google` | `ui/auth/SignInActivity` | Done |
| 4 | Auth: Facebook Sign-In | `src/pages/auth/Login.jsx` | Client OAuth / Token exchange | `ui/auth/SignInActivity` | Done |
| 5 | Auth: Remember Me & Credential Persistence | `src/pages/auth/Login.jsx` | EncryptedSharedPreferences | `utils/AppPreference` | Done |
| 6 | Auth: Forgot Password Dialog | `src/pages/auth/Login.jsx` | Client support info / backend | `ui/auth/SignInActivity` | Done |
| 7 | Auth: Session Expiry (401 Handling) | `src/services/api.js` | OkHttp Interceptor -> 401 | `data/remote/ApiClient` | Done |
| 8 | Auth: Logout & Session Purge | `src/context/AuthContext.jsx` | `POST /api/travel/auth/logout` | `TravellerMainActivity` / `ProfileFragment` | Done |
| 9 | Home: Hero Header & Greeting | `src/pages/tourist/TouristHome.jsx` | `GET /api/travel/auth/me` | `ui/traveller/HomeFragment` | Done |
| 10 | Home: Search & Quick Navigation | `src/pages/tourist/TouristHome.jsx` | In-app filter & search query | `ui/traveller/HomeFragment` | Done |
| 11 | Home: Featured Destinations Carousel | `src/pages/tourist/TouristHome.jsx` | `GET /api/travel/places?featured=true` | `ui/traveller/HomeFragment` | Done |
| 12 | Home: Categories Quick Browse | `src/pages/tourist/TouristHome.jsx` | `GET /api/travel/categories` | `ui/traveller/HomeFragment` | Done |
| 13 | Home: Provinces Quick Browse | `src/pages/tourist/TouristHome.jsx` | `GET /api/travel/provinces` | `ui/traveller/HomeFragment` | Done |
| 14 | Home: Popular Destinations List | `src/pages/tourist/TouristHome.jsx` | `GET /api/travel/places?sort_by=popular` | `ui/traveller/HomeFragment` | Done |
| 15 | Home: Emergency Contacts Hotline | `src/pages/tourist/TouristHome.jsx` | `GET /api/travel/settings` | `ui/traveller/HomeFragment` / `SupportActivity` | Done |
| 16 | Destinations: Full List & Grid | `src/pages/tourist/Destinations.jsx` | `GET /api/travel/places` | `ui/traveller/ExploreFragment` | Done |
| 17 | Destinations: Search by Keyword | `src/pages/tourist/Destinations.jsx` | `GET /api/travel/places?search=` | `ui/traveller/ExploreFragment` | Done |
| 18 | Destinations: Filter by Category | `src/pages/tourist/Destinations.jsx` | `GET /api/travel/places?category_id=` | `ui/traveller/ExploreFragment` | Done |
| 19 | Destinations: Filter by Province | `src/pages/tourist/Destinations.jsx` | `GET /api/travel/places?province_id=` | `ui/traveller/ExploreFragment` | Done |
| 20 | Destinations: Filter by Rating | `src/pages/tourist/Destinations.jsx` | `GET /api/travel/places?min_rating=` | `ui/traveller/ExploreFragment` | Done |
| 21 | Destinations: Sort (Popular/Rating/Newest/Name) | `src/pages/tourist/Destinations.jsx` | `GET /api/travel/places?sort_by=` | `ui/traveller/ExploreFragment` | Done |
| 22 | Destinations: Pagination & Swipe-to-Refresh | `src/pages/tourist/Destinations.jsx` | `GET /api/travel/places?page=` | `ui/traveller/ExploreFragment` | Done |
| 23 | Destination Detail: Overview & Header | `src/pages/tourist/DestinationDetails.jsx` | `GET /api/travel/places/{id}` | `ui/traveller/PlaceDetailActivity` | Done |
| 24 | Destination Detail: Image Gallery & Hero | `src/pages/tourist/DestinationDetails.jsx` | `GET /api/travel/places/{id}` | `ui/traveller/PlaceDetailActivity` | Done |
| 25 | Destination Detail: Best Time, Duration, Price | `src/pages/tourist/DestinationDetails.jsx` | `GET /api/travel/places/{id}` | `ui/traveller/PlaceDetailActivity` | Done |
| 26 | Destination Detail: Map Coordinates & Address | `src/pages/tourist/DestinationDetails.jsx` | `GET /api/travel/places/{id}` | `ui/traveller/PlaceDetailActivity` | Done |
| 27 | Destination Detail: Reviews List | `src/pages/tourist/DestinationDetails.jsx` | `GET /api/travel/places/{id}` | `ui/traveller/PlaceDetailActivity` | Done |
| 28 | Destination Detail: Submit Review | `src/pages/tourist/DestinationDetails.jsx` | `POST /api/travel/reviews` | `ui/traveller/WriteReviewDialog` | Done |
| 29 | Destination Detail: Book Visit / Guided Tour | `src/pages/tourist/DestinationDetails.jsx` | `POST /api/travel/bookings` | `ui/traveller/PlaceDetailActivity` | Done |
| 30 | Destination Detail: Wishlist Toggle | `src/pages/tourist/DestinationDetails.jsx` | `POST /api/travel/favorites/toggle/{id}` | `ui/traveller/PlaceDetailActivity` | Done |
| 31 | Destination Detail: Share Destination | `src/pages/tourist/DestinationDetails.jsx` | Android System Share Intent | `ui/traveller/PlaceDetailActivity` | Done |
| 32 | Categories Browsing Screen | `src/pages/tourist/Categories.jsx` | `GET /api/travel/categories` | `ui/category/CategoriesActivity` | Done |
| 33 | Provinces Browsing Screen | `src/pages/tourist/Provinces.jsx` | `GET /api/travel/provinces` | `ui/province/ProvincesActivity` | Done |
| 34 | Businesses: Directory List | `src/pages/tourist/Businesses.jsx` | `GET /api/travel/businesses` | `ui/business/BusinessesActivity` | Done |
| 35 | Businesses: Search & Filters | `src/pages/tourist/Businesses.jsx` | `GET /api/travel/businesses?search=&category_id=&province_id=` | `ui/business/BusinessesActivity` | Done |
| 36 | Business Detail: Info & Contacts | `src/pages/tourist/BusinessDetails.jsx` | `GET /api/travel/businesses/{id}` | `ui/business/BusinessDetailActivity` | Done |
| 37 | Business Detail: Services List & Service Booking | `src/pages/tourist/BusinessDetails.jsx` | `GET /api/travel/businesses/{id}/services`, `POST /api/travel/businesses/{id}/bookings` | `ui/business/BusinessDetailActivity` | Done |
| 38 | Business Detail: Opening Hours & Schedule | `src/pages/tourist/BusinessDetails.jsx` | `GET /api/travel/businesses/{id}/hours` | `ui/business/BusinessDetailActivity` | Done |
| 39 | Business Detail: Reviews & Submit Review | `src/pages/tourist/BusinessDetails.jsx` | `GET /api/travel/businesses/{id}/reviews`, `POST /api/travel/businesses/{id}/reviews` | `ui/business/BusinessDetailActivity` | Done |
| 40 | Bookings: My Bookings List | `src/pages/tourist/Bookings.jsx` | `GET /api/travel/bookings` | `ui/booking/BookingsFragment` | Done |
| 41 | Bookings: Filter by Status (Pending/Confirmed/Cancelled) | `src/pages/tourist/Bookings.jsx` | `GET /api/travel/bookings?status=` | `ui/booking/BookingsFragment` | Done |
| 42 | Bookings: Statistics Summary Header | `src/pages/tourist/Bookings.jsx` | `GET /api/travel/bookings` | `ui/booking/BookingsFragment` | Done |
| 43 | Bookings: Cancel Booking with Reason | `src/pages/tourist/Bookings.jsx` | `POST /api/travel/bookings/{id}/cancel` | `ui/booking/BookingDetailActivity` | Done |
| 44 | Bookings: Booking Details & Receipt | `src/pages/tourist/Bookings.jsx` | `GET /api/travel/bookings/{id}` | `ui/booking/BookingDetailActivity` | Done |
| 45 | Favorites: Wishlist Grid | `src/pages/tourist/Favorites.jsx` | `GET /api/travel/favorites` | `ui/favorite/FavoritesFragment` | Done |
| 46 | Favorites: Filter (All / Visited / Unvisited) | `src/pages/tourist/Favorites.jsx` | Client-side filter | `ui/favorite/FavoritesFragment` | Done |
| 47 | Favorites: Toggle Visited | `src/pages/tourist/Favorites.jsx` | `PATCH /api/travel/favorites/{id}/toggle-visited` | `ui/favorite/FavoritesFragment` | Done |
| 48 | Favorites: Remove Destination | `src/pages/tourist/Favorites.jsx` | `DELETE /api/travel/favorites/{placeId}` | `ui/favorite/FavoritesFragment` | Done |
| 49 | Profile: User Info & Verification Status | `src/pages/tourist/Profile.jsx` | `GET /api/travel/auth/me` | `ui/profile/ProfileFragment` | Done |
| 50 | Profile: Edit Profile (Name, Phone, Location, Bio) | `src/pages/tourist/Profile.jsx` | `PUT /api/travel/auth/profile` | `ui/profile/EditProfileActivity` | Done |
| 51 | Profile: Change Password | `src/pages/tourist/Profile.jsx` | `PUT /api/travel/auth/password` | `ui/profile/ChangePasswordActivity` | Done |
| 52 | Profile: Avatar Upload | `src/pages/tourist/Profile.jsx` | `POST /api/travel/auth/avatar` | `ui/profile/ProfileFragment` | Done |
| 53 | Notifications: Notification Feed | `src/pages/tourist/Notifications.jsx` | `GET /api/travel/notifications` | `ui/notification/NotificationsActivity` | Done |
| 54 | Notifications: Unread Count Badge | `src/pages/tourist/Notifications.jsx` | `GET /api/travel/notifications/unread-count` | `ui/notification/NotificationsActivity` | Done |
| 55 | Notifications: Mark Read & Mark All Read | `src/pages/tourist/Notifications.jsx` | `PATCH /api/travel/notifications/{id}/read`, `read-all` | `ui/notification/NotificationsActivity` | Done |
| 56 | Notifications: Delete Notification | `src/pages/tourist/Notifications.jsx` | `DELETE /api/travel/notifications/{id}` | `ui/notification/NotificationsActivity` | Done |
| 57 | Settings: Localization (Khmer / English) | Web Language Switcher | Android LocaleManager / Configuration | `ui/settings/SettingsActivity` | Done |
| 58 | Settings: Theme Mode (Light / Dark) | Web Theme Toggle | AppCompatDelegate DayNight | `ui/settings/SettingsActivity` | Done |
| 59 | Support & FAQs Screen | `src/pages/tourist/Support.jsx` | `GET /api/travel/settings`, `POST /api/travel/contact` | `ui/support/SupportActivity` | Done |
| 60 | AI Chat Assistant (AngkorVers) | `src/pages/tourist/AIChat.jsx` | `POST /api/travel/ai/chat`, `GET /api/travel/ai/suggestions` | `ui/aichat/AIChatActivity` | Done |
| 61 | Business Owner: Main Shell & Business Switcher | `src/pages/business-owner/*` | `GET /api/business/businesses` | `ui/business/BusinessMainActivity` | Done |
| 62 | Business Owner: Dashboard & KPIs | `src/pages/business-owner/Dashboard.jsx` | `GET /api/business/dashboard`, `bookings/stats` | `ui/business/BusinessDashboardFragment` | Done |
| 63 | Business Owner: Services Management (CRUD) | `src/pages/business-owner/Listings.jsx`, `ServicesTab.jsx` | `GET/POST/PUT/DELETE /api/business/services` | `ui/business/BusinessListingsFragment` | Done |
| 64 | Business Owner: Promotions & Discounts (CRUD) | `src/pages/business-owner/Listings.jsx`, `PromotionsTab.jsx` | `GET/POST/PUT/DELETE /api/business/promotions` | `ui/business/BusinessListingsFragment` | Done |
| 65 | Business Owner: Photo Gallery Management | `src/pages/business-owner/Listings.jsx`, `PhotosTab.jsx` | `GET/POST/DELETE /api/business/images` | `ui/business/BusinessListingsFragment` | Done |
| 66 | Business Owner: Bookings Queue & Status Actions | `src/pages/business-owner/Bookings.jsx` | `GET /api/business/bookings`, `PATCH status` | `ui/business/BusinessBookingsFragment` | Done |
| 67 | Business Owner: Booking Detail View | `src/pages/business-owner/Bookings.jsx` | `GET /api/business/bookings/{id}` | `ui/business/BusinessBookingDetailActivity` | Done |
| 68 | Business Owner: Customer Reviews & Owner Replies | `src/pages/business-owner/Reviews.jsx` | `GET /api/business/reviews`, `POST reply` | `ui/business/BusinessReviewsFragment` | Done |
| 69 | Business Owner: Weekly Operating Hours | `src/pages/business-owner/OperatingHours.jsx` | `GET/POST /api/business/hours` | `ui/business/BusinessHoursActivity` | Done |
| 70 | Business Owner: Sales & Analytics Reports | `src/pages/business-owner/Reports.jsx` | `GET /api/business/reports` | `ui/business/BusinessReportsActivity` | Done |
| 71 | Business Owner: Statement CSV Export | `src/pages/business-owner/Reports.jsx` | Client-generated CSV / FileProvider | `ui/business/BusinessReportsActivity` | Done |
| 72 | Business Owner: Business Profile Editor | `src/pages/business-owner/Profile.jsx` | `GET/PUT /api/business/profile` | `ui/business/BusinessEditProfileActivity` | Done |
| 73 | Business Owner: Manage Businesses (Multi-venue) | `src/components/business/BusinessSelectorModal.jsx` | `GET /api/business/businesses` | `ui/business/ManageBusinessesActivity` | Done |
| 74 | Business Owner: Notifications Feed & Badge | `src/pages/business-owner/Notifications.jsx` | `GET /api/business/notifications`, `PATCH read` | `ui/business/BusinessNotificationsActivity` | Done |
| 75 | Business Owner: Settings & Password Change | `src/pages/business-owner/Settings.jsx` | `PUT /api/travel/auth/password` | `ui/business/BusinessSettingsActivity` | Done |
| 76 | Business Owner: Business Hub (More Menu) | `src/pages/business-owner/*` | Navigation Hub | `ui/business/BusinessMoreFragment` | Done |

