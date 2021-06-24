json.extract! submit, :id, :title, :URL, :text, :user_id, :like, :created_at, :updated_at
json.url submit_url(submit, format: :json)
