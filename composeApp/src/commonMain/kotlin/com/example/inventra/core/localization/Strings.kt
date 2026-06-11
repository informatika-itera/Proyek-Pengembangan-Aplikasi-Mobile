package com.example.inventra.core.localization

interface Strings {
    val appName: String
    val catalog: String
    val history: String
    val profile: String
    val aiAssistant: String
    val home: String
    
    // Dashboard
    val dashboardOverview: String
    val dashboardSubtitle: String
    val totalItems: String
    val borrowed: String
    val available: String
    val activeBorrowing: String
    val registerNewItem: String
    val registerNewItemDesc: String
    val getStarted: String
    val borrower: String
    val dueDate: String
    val overdueAlert: String
    val takeAction: String
    val failedToLoadData: String
    
    // Profile Screen
    val myProfile: String
    val editProfile: String
    val fullName: String
    val phoneNumber: String
    val cancel: String
    val save: String
    val darkMode: String
    val accountManagement: String
    val resetAllData: String
    val logout: String
    val language: String
    val changeLanguage: String
    val confirmResetTitle: String
    val confirmResetMessage: String
    
    // History / Borrowing
    val borrowing: String
    val pending: String
    val active: String
    val all: String
    val returned: String
    val overdue: String
    val waitingApproval: String
    val approveBorrow: String
    val approveReturn: String
    val returnItem: String
    val returnDate: String
    val fine: String
    val noHistoryTitle: String
    val noHistoryDesc: String
    val noPendingTitle: String
    val noPendingDesc: String
    val noActiveTitle: String
    val noActiveDesc: String
    val pendingActionDesc: String
    
    // Items & Detail
    val itemName: String
    val description: String
    val location: String
    val totalStock: String
    val availableStock: String
    val category: String
    val condition: String
    val pic: String
    val picPhone: String
    val itemInformation: String
    val loanPolicy: String
    val maxDuration: String
    val overdueFine: String
    val admin: String
    val itemNotFound: String
    val contactPIC: String
    val borrow: String
    val outOfStock: String
    val deleteItem: String
    val deleteItemConfirm: String
    val requestBorrow: String
    val requestBorrowDesc: String
    val borrowerNameLabel: String
    val submitRequest: String
    val requestSent: String
    val locationNotSet: String

    // Item Conditions
    val condNew: String
    val condGood: String
    val condFair: String
    val condPoor: String
    
    // Categories
    val catAll: String
    val catMedical: String
    val catElectronics: String
    val catFlag: String
    val catFood: String
    val catOther: String
    
    // AI Assistant
    val aiInventoryAssistant: String
    val aiAnalyzeStock: String
    val aiAnalyzeStockDesc: String
    val aiSuggestProcurement: String
    val aiSuggestProcurementDesc: String
    val aiBorrowingReport: String
    val aiBorrowingReportDesc: String
    val aiOverdueAction: String
    val aiOverdueActionDesc: String
    val aiCustomQuery: String
    val aiCustomQueryDesc: String
    val aiCustomQueryHint: String
    val processing: String
    val runAnalysis: String
    val analysisResult: String
    val aiEmptyState: String
    val aiSystemContext: String
    
    // Auth
    val login: String
    val selectDivision: String
    val adminAccess: String
    val email: String
    val password: String
    val forgotPassword: String
    val loginButton: String
    val dontHaveAccount: String
    val contactAdminForAccount: String
    val requestAccountFromAdmin: String
    val welcomeBack: String
    val loginSubtitle: String
    val loginOrganization: String

    // Actions & Messages
    val searchItem: String
    val noItemFound: String
    val noItemInKategory: String
    val addPhotoTitle: String
    val addPhotoDescription: String
    val changePhoto: String
    val deletePhoto: String
    val uploadingPhoto: String
    val photoSupportedFormats: String
    val camera: String
    val gallery: String
    val choosePhotoSource: String
    val choosePhotoSourceDesc: String
    val returnProofTitle: String
    val returnProofDesc: String
    val close: String
    val adminHead: String
    val members: String
    val noMembers: String
    val createAndManageAccountDesc: String
    val resetDataDesc: String
    val resetDataSuccess: String
    val resetDataError: String
    val darkThemeActive: String
    val darkThemeInactive: String
    val settings: String
    val profileUpdated: String
    val profilePhotoUpdated: String
}
