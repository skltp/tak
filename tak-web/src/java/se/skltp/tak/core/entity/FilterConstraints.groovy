package se.skltp.tak.core.entity

constraints = {
	servicedomain(blank:false, nullable:false, maxSize: 255, unique: ['anropsbehorighet'])
	anropsbehorighet(nullable: false)
}
