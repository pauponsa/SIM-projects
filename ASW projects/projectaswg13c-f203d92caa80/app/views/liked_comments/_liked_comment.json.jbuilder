json.extract! liked_comment, :id, :user_id, :comentari_id, :created_at, :updated_at
json.url liked_comment_url(liked_comment, format: :json)
