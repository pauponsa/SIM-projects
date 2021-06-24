class Evento < ApplicationRecord

	has_many :entrada_usuarios
  has_many :event_tags
  has_many :tag, :through => :event_tags
	has_many :event_images
	has_many :favourites
	attr_accessor :event_image_data

	def formatted_data
	{
			:id         	=> self.id,
			:title 				=> self.title,
			:description 	=> self.description,
			:start_date		=> self.start_date,
			:end_date			=> self.end_date,
			:capacity			=> self.capacity,
			:latitude			=> self.latitude,
			:longitude		=> self.longitude,
			:participants	=> self.participants,
			:price	=> self.price,
			:URL_share	=> self.URL_share,
			:URL_page	=> self.URL_page,
			:start_time	=> self.start_time,
			:end_time	=> self.end_time,
			:id_creator	=> self.id_creator,
			:images_url => self.get_event_images,
			:author => self.author
	}
	end

	def get_event_images
		result = {}
		count = 0
    self.event_images.each do |image|
      result[count] = image.image.url
			count = count+1
    end
    return result
	end
end
