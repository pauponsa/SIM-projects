class CreateLikedPosts < ActiveRecord::Migration[6.1]
  def change
    create_table :liked_posts do |t|
      t.references :submit, null: false, foreign_key: true
      t.references :users, null: false, foreign_key: true

      t.timestamps
    end
  end
end
