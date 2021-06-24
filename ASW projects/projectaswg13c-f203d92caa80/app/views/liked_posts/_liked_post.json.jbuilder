json.extract! liked_post, :id, :submit_id, :user_id, :created_at, :updated_at
json.url liked_post_url(liked_post, format: :json)
