This project demonstrates the UI part implementation of paginated lists. The two implementations are included. One for RecyclerView and another one for ListView. Both implementation uses some shareable code and resources for "load more" list item.

Pagination is implemented on adapter level. For the ListView case the "decorator" pattern is used by the wrapper-adapter, which manages "load more" view item and may be applied to any descendant of BaseAdapter.
The same approach is used in the SDK to support Headers/Footers (by the HeaderViewListAdapter).

For the RecyclerView case the abstract descendant of the RecyclerAdapter is implemented to support "Load more" logic. This adapter must be extended by a final adapter implementation.

The project is splited on several modules. Module "listpagination" contains all classes and resources which essentially implement pagination pagination pattern: PaginatedListAdapter, PaginatedRecyclerAdapter, LoadMoreView


The main "app" module contains launcher screen and demo framework for the paginated lists, which includes:
demo activities (*BaseDemoActivit*, ListPaginationDemoActivity, RecyclerPaginationDemoActivity);
demo adapters (MyListInnerAdapter, MyPaginatedRecyclerAdapter);
test data source (ListItemModel, PaginatedPresenter, DelayedDataSourceEmulator, CommunicationErrorEmulator).


This project also demonstrates the implementation of MVP application architecture pattern see combination of:
- LauncherActivity+LauncherActivityPresenter+LauncherScreenView
- ListPaginationDemoActivity+RecyclerPaginationDemoActivity+IPaginatedListView+PaginatedPresenter)

It also demonstrates using of the Android Data binding library:
- LauncherActivity + activity_launcher.xml
- LauncherAdapter + layncher_list_item.xml