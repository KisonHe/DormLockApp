An App to work with DormLock Project to open door

## Update Plans
1. Finish Admin fragment related functions like
   1. Change Password of any user
   2. Set servo Up and Down Limits
2. Use [AndroidX Preference Library](https://developer.android.com/guide/topics/ui/settings)
   1. Remove the admin fragment, use `Control Preference visibility`


## Known Bug
1. In use of volley and NavController, if navigated to another fragment then the volley handler from last fragment
calls NavController, things will crash