<p align="center">
  <img src="https://www.corda.net/wp-content/uploads/2016/11/fg005_corda_b.png" alt="Corda" width="500">
</p>

### Persist hierarchical relationships
    
      @Entity
         @CordaSerializable
         @Table(name = "parent_data")
         class PersistentParentToken(
                 @Column(name = "owner")
                 var owner: String,
     
                 @Column(name = "issuer")
                 var issuer: String,
     
                 @Column(name = "amount")
                 var currency: Int,
     
                 @Column(name = "linear_id")
                 var linear_id: UUID,
     
                 @OneToOne(fetch = FetchType.LAZY)
                 @JoinColumns(JoinColumn(name = "transaction_id", referencedColumnName = "transaction_id"), JoinColumn(name = "output_index", referencedColumnName = "output_index"))
     
                 @OrderColumn
                 @Cascade(CascadeType.PERSIST)
                 var listOfPersistentChildTokens: PersistentChildToken?
     
         ) : PersistentState()
     
         @Entity
         @Suppress("unused")
         @Table(name = "child_data")
         class PersistentChildToken(
     
                 @Column(name = "child_id", unique = true, nullable = false)
                 var childId: Int? = null,
     
                 @Column(name = "owner")
                 var owner: String,
     
                 @Column(name = "issuer")
                 var issuer: String,
     
                 @Column(name = "amount")
                 var currency: Int,
     
                 @Column(name = "linear_id")
                 var linear_id: UUID,
     
                 @OneToOne(fetch = FetchType.LAZY)
                 @JoinColumns(JoinColumn(name = "transaction_id", referencedColumnName = "transaction_id"), JoinColumn(name = "output_index", referencedColumnName = "output_index"))
                 var parent: PersistentParentToken ? = null
     
     
         ) : PersistentState() {
             constructor() : this(0,"","",0, UUID.randomUUID(),parent = null)
         }
  
  ##Under development for ManytoOne