package se.skltp.tak.core.entity

constraints = {
	category(blank:false, nullable:false, maxSize: 255, unique: ['filter', 'deleted'], validator: { val, obj ->
		
		if (val?.startsWith(" ")) {
			return 'invalid.leadingspace'
		}
		if (val?.startsWith("\t")) {
			return 'invalid.leadingtab'
		}
		if (val?.endsWith(" ")) {
			return 'invalid.trailingspace'
		}
		if (val?.endsWith("\t")) {
			return 'invalid.trailingtab'
		}
        
        return true
    })
	
	filter(nullable: false)
}
