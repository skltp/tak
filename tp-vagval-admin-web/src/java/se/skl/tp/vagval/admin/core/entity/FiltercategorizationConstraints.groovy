package se.skl.tp.vagval.admin.core.entity

constraints = {
	category(blank:false, nullable:false, maxSize: 255, unique: ['filter'])
	filter(nullable: false)
}
