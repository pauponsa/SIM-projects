class CreateUsuariReports < ActiveRecord::Migration[6.1]
  def change
    create_table :usuari_reports do |t|
       	t.belongs_to :user
      	t.belongs_to :evento       
    end
  end
end
