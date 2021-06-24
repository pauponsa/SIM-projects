require "test_helper"

class RatingsControllerTest < ActionDispatch::IntegrationTest
  setup do
    @rating = ratings(:one)
    @evento = eventos(:two)
  end

  test "should get index" do
    get ratings_url, as: :json
    assert_response :success
  end

  test "should create rating" do
    post users_url, params: { email: "company@gmail.com", password: "123456789", password_confirmation: "123456789", role: "company" }, as: :json
    company_resp=JSON.parse(@response.body)
    assert_response :success

    post '/login', params: { email: "company@gmail.com", password: "123456789" }, as: :json
    assert_response :success
    login_response = JSON.parse(@response.body)

    post eventos_url, params: { token: login_response["login_token"],  title: @evento.title, description: @evento.description , start_date: @evento.start_date, end_date: @evento.end_date, capacity: @evento.capacity , latitude: @evento.latitude, longitude:@evento.longitude, price: @evento.price, start_time: @evento.start_time, end_time: @evento.end_time, id_creator: company_resp["id"] }, as: :json
    assert_response :success
    evento = JSON.parse(@response.body)

    #Then we will create a new user
    post users_url, params: { email: "customer@gmail.com", password: "123456789", password_confirmation: "123456789", role: "customer" }, as: :json
    customer_resp = JSON.parse(@response.body)
    assert_response :success

    post '/login', params: { email: "customer@gmail.com", password: "123456789" }, as: :json
    assert_response :success
    login_response = JSON.parse(@response.body)

    assert_difference('Rating.count') do
      post ratings_url, params: { rating: @rating.rating, text: @rating.text, company_id: company_resp["id"], token: login_response["login_token"], customer_id: login_response["id"], evento_id: evento["id"]}, as: :json
    end

    assert_response :success
  end

  test "should show rating" do
    get rating_url(@rating), as: :json
    assert_response :success
  end

  test "should destroy rating" do
    post users_url, params: { email: "company@gmail.com", password: "123456789", password_confirmation: "123456789", role: "company" }, as: :json
    company_resp=JSON.parse(@response.body)
    assert_response :success

    post '/login', params: { email: "company@gmail.com", password: "123456789" }, as: :json
    assert_response :success
    login_response = JSON.parse(@response.body)

    post eventos_url, params: { token: login_response["login_token"],  title: @evento.title, description: @evento.description , start_date: @evento.start_date, end_date: @evento.end_date, capacity: @evento.capacity , latitude: @evento.latitude, longitude:@evento.longitude, price: @evento.price, start_time: @evento.start_time, end_time: @evento.end_time, id_creator: company_resp["id"] }, as: :json
    assert_response :success
    evento = JSON.parse(@response.body)

    #Then we will create a new user
    post users_url, params: { email: "customer@gmail.com", password: "123456789", password_confirmation: "123456789", role: "customer" }, as: :json
    customer_resp = JSON.parse(@response.body)
    assert_response :success

    post '/login', params: { email: "customer@gmail.com", password: "123456789" }, as: :json
    assert_response :success
    login_response = JSON.parse(@response.body)

    assert_difference('Rating.count') do
      post ratings_url, params: { rating: @rating.rating, text: @rating.text, company_id: company_resp["id"], token: login_response["login_token"], customer_id: login_response["id"], evento_id: evento["id"]}, as: :json
    end
    rat_resp=JSON.parse(@response.body)

    assert_difference('Rating.count', -1) do
      delete rating_url(rat_resp["id"]), as: :json
    end

    assert_response 204
  end
end
