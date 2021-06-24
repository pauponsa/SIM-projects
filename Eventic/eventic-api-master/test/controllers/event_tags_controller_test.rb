require "test_helper"

class EventTagsControllerTest < ActionDispatch::IntegrationTest
  setup do
     @event_tag = event_tags(:one)
  end
  
  test "should get index" do
     get event_tags_url, as: :json
     assert_response :success
  end
  
  test "should create event_tag" do
  #first we will create a new user
    post users_url, params: { email: "companyUpdate@gmail.com", password: "123456789", password_confirmation: "123456789", role: "company" }, as: :json
    assert_response :success
    company_response = JSON.parse(@response.body)
    #then we will log him up
    post '/login', params: { email: "companyUpdate@gmail.com", password: "123456789" }, as: :json
    assert_response :success
    login_response = JSON.parse(@response.body)
    #once the user is created we create a new event
    assert_difference('Evento.count') do
      post eventos_url, params: { token: login_response["login_token"], id_creator: company_response["id"], title: "wenas", description: "qtal", start_date: "20-04-2021", end_date: "25-04-2021" }, as: :json
    end
    assert_response 201
    event_id = JSON.parse(@response.body)["id"]
    #lets create a tag
    assert_difference('Tag.count') do
      post tags_url, params: { tag: { tag_name: "NEWTAG" } }, as: :json
    end
    assert_response 201
    t_id = JSON.parse(@response.body)["id"]
    assert_difference('EventTag.count') do
      post event_tags_url, params: { evento_id: event_id, tag_id: t_id } , as: :json
    end
    assert_response 201
  end
  
   test "should show event_tag" do
     get event_tag_url(@event_tag), as: :json
     assert_response :success
  end
  
   test "should update event_tag" do
     #first we will create a new user
     post users_url, params: { email: "companyUpdate@gmail.com", password: "123456789", password_confirmation: "123456789", role: "company" }, as: :json
     assert_response :success
     company_response = JSON.parse(@response.body)
     #then we will log him up
     post '/login', params: { email: "companyUpdate@gmail.com", password: "123456789" }, as: :json
     assert_response :success
     login_response = JSON.parse(@response.body)
     #once the user is created we create a new event
     assert_difference('Evento.count') do
       post eventos_url, params: { token: login_response["login_token"], id_creator: company_response["id"], title: "wenas", description: "qtal", start_date: "20-04-2021", end_date: "25-04-2021" }, as: :json
     end
     assert_response 201
     event_id = JSON.parse(@response.body)["id"]
     #lets create a tag
     assert_difference('Tag.count') do
       post tags_url, params: { tag: { tag_name: "NEWTAG" } }, as: :json
     end
     assert_response 201
     t_id = JSON.parse(@response.body)["id"]
     patch event_tag_url(@event_tag), params: { evento_id: event_id, tag_id: t_id }, as: :json
     assert_response 200
   end
  
   test "should destroy event_tag" do
    #first we will create a new user
    post users_url, params: { email: "companyUpdate@gmail.com", password: "123456789", password_confirmation: "123456789", role: "company" }, as: :json
    assert_response :success
    company_response = JSON.parse(@response.body)
    #then we will log him up
    post '/login', params: { email: "companyUpdate@gmail.com", password: "123456789" }, as: :json
    assert_response :success
    login_response = JSON.parse(@response.body)
    #once the user is created we create a new event
    assert_difference('Evento.count') do
      post eventos_url, params: { token: login_response["login_token"], id_creator: company_response["id"], title: "wenas", description: "qtal", start_date: "20-04-2021", end_date: "25-04-2021" }, as: :json
    end
    assert_response 201
    event_id = JSON.parse(@response.body)["id"]
    #lets create a tag
    assert_difference('Tag.count') do
      post tags_url, params: { tag: { tag_name: "NEWTAG" } }, as: :json
    end
    assert_response 201
    t_id = JSON.parse(@response.body)["id"]
    assert_difference('EventTag.count') do
      post event_tags_url, params: { evento_id: event_id, tag_id: t_id } , as: :json
    end
    assert_response 201
     assert_difference('EventTag.count', -1) do
       delete '/event_tags', params: {evento_id: event_id, tag_id: t_id}, as: :json
     end
  
     assert_response 204
   end
end
