json.extract! comentari, :id, :text, :user, :postID, :respondsToId, :created_at, :updated_at
json.url comentari_url(comentari, format: :json)
