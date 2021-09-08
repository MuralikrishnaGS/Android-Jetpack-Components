package com.samruddhi.mvvmarchitecturedatabindcoroutinesretrofit.model

data class LocationModel(val longitude: Double, val latitude: Double)

data class SearchRestaurantsUsingLatLon(
    val location: RestaurantsSearchLocation,
    val popularity: RestaurantsSearchPopularity,
    val link: String,
    val nearby_restaurants: Restaurants
)

data class RestaurantsSearchLocation(
    val entity_type: String,
    val entity_id: Int,
    val title: String,
    val latitude: String,
    val longitude: String,
    val city_id: Int,
    val city_name: String,
    val country_id: Int,
    val country_name: String
)

data class RestaurantsSearchPopularity(
    val popularity: String,
    val nightlife_index: Double,
    val nearby_res: String,
    val top_cuisines: String,
    val popularity_res: String,
    val nightlife_res: String,
    val subzone: String,
    val subzone_id: Int,
    val city: String
)

data class SearchRestaurantsUsingLatLonRange(
    val results_found: Int,
    val results_start: Int,
    val results_shown: Int,
    val restaurants: List<RestaurantsDataList>
)

data class RestaurantsDataList(val restaurant: Restaurants)

data class Restaurants(
    val apikey: String,
    val id: Int,
    val name: String,
    val url: String,
    val location: RestaurantsLocation,
    val switch_to_order_menu: Int,
    val cuisines: String,
    val timings: String,
    val average_cost_for_two: String,
    val price_range: String,
    val currency: String,
    val highlights: Array<String>,
    val offers: Array<String>,
    val opentable_support: String,
    val is_zomato_book_res: String,
    val mezzo_provider: String,
    val is_book_form_web_view: String,
    val book_form_web_view_url: String,
    val book_again_url: String,
    val thumb: String,
    val user_rating: UserRating,
    val all_reviews_count: Int,
    val photos_url: String,
    val photo_count: Int,
    val menu_url: String,
    val featured_image: String,
    val medio_provider: String,
    val has_online_delivery: String,
    val is_delivering_now: String,
    val store_type: String,
    val include_bogo_offer: Boolean,
    val deeplink: String,
    val order_url: String,
    val order_deeplink: String,
    val is_table_reservation_supported: String,
    val has_table_booking: String,
    val events_url: String,
    val phone_numbers: String,
    val all_reviews: Reviews,
    val establishment: Array<String>,
    val establishment_types: Array<String>
)

data class RestaurantsLocation(
    val address: String,
    val locality: String,
    val city: String,
    val city_id: Int,
    val latitude: Double,
    val longitude: Double,
    val zipcode: String,
    val country_id: Int,
    val locality_verbose: String
)

data class UserRating(
    val aggregate_rating: String,
    val rating_text: String,
    val rating_color: String,
    val rating_obj: RatingObj,
    val votes: String
)

data class RatingObj(val title: RatingTitle, val bg_color: BackGroundColor)

data class RatingTitle(val text: String)

data class BackGroundColor(val type: String, val tint: String)

data class Reviews(val review: String)