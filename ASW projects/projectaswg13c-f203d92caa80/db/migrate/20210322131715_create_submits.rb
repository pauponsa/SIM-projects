class CreateSubmits < ActiveRecord::Migration[6.1]
  def change
    create_table :submits do |t|
      t.string :title
      t.string :URL
      t.string :text
      t.string :creator
      t.integer :like

      t.timestamps
    end
  end
end
