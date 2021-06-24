class ChangeLikedPostsColum < ActiveRecord::Migration[6.1]
  def change
    rename_column :liked_posts, :users_id, :user_id
    rename_column :liked_comments, :users_id, :user_id
  end
end
