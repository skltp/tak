package se.skl.tp.vagval.admin.core.entity

constraints = {
	servicedomain(blank:false, nullable:false, maxSize: 255, unique: ['anropsbehorighet'])
	anropsbehorighet(nullable: false)
}
