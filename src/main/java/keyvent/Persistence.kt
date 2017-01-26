package keyvent

interface EventRepository<ID> {
    fun eventsAfter(id: ID, afterVersion: Version): List<UnitOfWork>
}

interface Journal<ID> {
    fun append(targetId: ID, unitOfWork: UnitOfWork)
}

// default implementations - just for tests -- TODO caffeine ?

class MapEventRepository<ID>(val map: MutableMap<ID, MutableList<UnitOfWork>> = mutableMapOf(),
                             val versionExtractor: (UnitOfWork) -> Version)
    : EventRepository<ID> {
    override fun eventsAfter(id: ID, afterVersion: Version): List<UnitOfWork> {
        val targetInstance = map[id]
        return targetInstance?.filter { uow -> versionExtractor(uow).version > afterVersion.version } ?: listOf()
    }

}

class MapJournal<ID>(val map: MutableMap<ID, MutableList<UnitOfWork>> = mutableMapOf(),
                     val versionExtractor: (UnitOfWork) -> Version) : Journal<ID> {
    override fun append(targetId: ID, unitOfWork: UnitOfWork) {
        val targetInstance = map[targetId]
        if (targetInstance == null) {
            require(versionExtractor(unitOfWork) == Version(1))
            map.put(targetId, mutableListOf((unitOfWork)))
        } else {
            val lastVersion = versionExtractor(targetInstance.last())
            require(versionExtractor(unitOfWork) == lastVersion.nextVersion())
            targetInstance.add(unitOfWork)
        }
    }
}
