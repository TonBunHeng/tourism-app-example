# Manual Testing Checklist

This checklist verifies all features of `smarttourismapp` running against the backend REST API (`http://10.0.2.2:8000/api/travel/` on Android Emulator).

## 1. Foundation & Connectivity
- [ ] Base URL points to `http://10.0.2.2:8000/api/travel/` in debug builds.
- [ ] OkHttp interceptor automatically adds `Authorization: Bearer <token>` on all requests once authenticated.
- [ ] 401 Unauthorized response triggers automatic session purge and navigation to `SignInActivity`.
- [ ] Network timeout and disconnection errors display friendly, actionable error messages with retry options.
- [ ] Laravel 422 validation errors are parsed and displayed on corresponding form fields (`TextInputLayout.setError`).

## 2. Authentication Flow
- [ ] **Sign In**: Valid email/password navigates user to `TravellerMainActivity` (or `BusinessMainActivity` for business accounts).
- [ ] **Sign In Error**: Invalid credentials show clean error message without leaking SQL or stack traces.
- [ ] **Remember Me**: Checking Remember Me saves email and restores it upon next app launch.
- [ ] **Google Sign-In**: Tapping Google button launches standard Google Play Services OAuth picker.
- [ ] **Register**: Toggle between Traveler and Business Owner role; all fields validate (name, email, 8+ char password matching confirmation, optional phone/location/bio).
- [ ] **Forgot Password**: Tapping 'Forgot Password' opens clear reset instructions dialog.
- [ ] **Sign Out**: Tapping Sign Out in profile or app bar clears EncryptedSharedPreferences and returns to `SignInActivity`.

## 3. Home Screen
- [ ] Greeting displays logged-in user name and location.
- [ ] Hero banner renders with Cambodia tourism imagery and search shortcut.
- [ ] Category chips load horizontally and filter destinations when tapped.
- [ ] Province cards load horizontally and filter destinations when tapped.
- [ ] Featured Destinations carousel displays top attractions with rating, category, and province.
- [ ] Popular Destinations list displays items with heart icon indicating wishlist status.
- [ ] Emergency hotline banner displays emergency numbers (Police: 117, Fire: 118, Medical: 119) with one-tap dialing.
- [ ] AI Assistant FAB button opens full AngkorVers AI chat.

## 4. Destinations / Explore Screen
- [ ] Live search bar filters attractions with debounce / keyboard action.
- [ ] Filter dialog/chips allow selecting Category, Province, Min Rating, and Sort By (Popular, Rating, Newest, Name).
- [ ] Swipe-to-refresh reloads fresh destinations from database.
- [ ] Tapping any place card opens `PlaceDetailActivity`.
- [ ] Tapping heart icon adds/removes item from user's wishlist with instant visual feedback and toast.

## 5. Place Detail Screen
- [ ] Cover image renders via Glide with rounded corners and placeholder.
- [ ] Title, category, province, rating, and review count displayed accurately.
- [ ] Admission fee, best time to visit, and recommended duration chips rendered.
- [ ] Full description text displayed cleanly with no overflow.
- [ ] Address and map pin shown with option to open Google Maps navigation.
- [ ] Reviews list displayed with reviewer avatar, name, star rating, date, and review comment.
- [ ] "Write a Review" button opens rating dialog with 1-5 stars and comment input, submitting to `POST /api/travel/reviews`.
- [ ] "Book Visit" button opens booking modal with date, time, guest count, and notes, submitting to `POST /api/travel/bookings`.
- [ ] Share button triggers system share sheet with place title, location, and description.

## 6. Businesses & Directory
- [ ] Businesses list displays hotels, restaurants, and tourism services with photos, ratings, category, and province.
- [ ] Filtering by category and province works smoothly.
- [ ] Business Detail displays business overview, services with prices, opening hours, photo gallery, and reviews.
- [ ] "Book Service" button allows reserving a service with guest count and date.
- [ ] "Review Business" button submits verified traveler feedback to `POST /api/travel/businesses/{id}/reviews`.

## 7. Bookings Management
- [ ] Bookings tab displays user's reservations with status badges (Pending, Confirmed, Completed, Cancelled).
- [ ] Status filter tabs (All, Pending, Confirmed, Completed, Cancelled) filter list accurately.
- [ ] Summary statistics card shows count of total, pending, and confirmed bookings.
- [ ] Tapping a booking card opens `BookingDetailActivity` with booking reference, date, time, service details, and pricing.
- [ ] "Cancel Booking" button prompts for confirmation and cancellation reason, updating status to Cancelled.

## 8. Wishlist / Favorites
- [ ] Saved destinations appear in grid/list format.
- [ ] Filter chips allow filtering by All, Visited, and Unvisited.
- [ ] Tapping checkmark icon toggles Visited status via `PATCH /api/travel/favorites/{id}/toggle-visited`.
- [ ] Tapping delete/trash icon removes destination from wishlist with confirmation dialog.

## 9. Profile & Settings
- [ ] User profile displays avatar, name, email, role badge, location, and bio.
- [ ] "Edit Profile" allows updating name, phone, location, and bio via `PUT /api/travel/auth/profile`.
- [ ] "Change Password" requires current password, validates 8+ char new password, and confirms via `PUT /api/travel/auth/password`.
- [ ] Avatar image picker allows selecting local photo and uploads via multipart `POST /api/travel/auth/avatar`.
- [ ] Language switch toggles between English and Khmer (values-km).
- [ ] Theme toggle switches between Light and Dark Material 3 theme.

## 10. Notifications
- [ ] Bell icon in toolbar displays badge count for unread notifications.
- [ ] Notifications screen displays list of notifications with time, title, description, and status.
- [ ] Tapping an unread notification marks it as read.
- [ ] "Mark All Read" button updates all notifications to read status.
- [ ] Swipe or trash button deletes individual notifications.

## 11. Support & AI Assistant
- [ ] Support screen displays Emergency Contacts (Police 117, Medical 119, Fire 118, Tourist Hotline).
- [ ] FAQ accordion expands and collapses answers to popular tourist questions.
- [ ] Contact Us form validates name, email, subject, message and submits via `POST /api/travel/contact`.
- [ ] AI Assistant screen allows conversational chatting with suggested prompt chips and quick replies.
