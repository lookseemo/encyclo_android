package com.deedsit.android.bookworm.arch;

import com.deedsit.android.bookworm.BookWormApplication;
import com.deedsit.android.bookworm.ui.MainActivity;
import com.deedsit.android.bookworm.ui.SignInActivity;
import com.deedsit.android.bookworm.ui.authentication.UserAuthFragment;
import com.deedsit.android.bookworm.ui.mainactivityfragments.HomeFragmentStudent;
import com.deedsit.android.bookworm.ui.mainactivityfragments.RatingFragmentLecturer;
import com.deedsit.android.bookworm.ui.presenter.LectureHomePresenter;
import com.deedsit.android.bookworm.ui.presenter.LectureRatingPresenter;
import com.deedsit.android.bookworm.ui.presenter.LecturerViewClassPresenter;
import com.deedsit.android.bookworm.ui.presenter.StudentHomePresenter;
import com.deedsit.android.bookworm.ui.presenter.StudentListingPresenter;
import com.deedsit.android.bookworm.ui.presenter.StudentRatingPresenter;

/**
 * Created by steph on 11/11/2017.
 */

public interface AppGraph {
    void inject(BookWormApplication app);

    void inject(SignInActivity signInActivity);

    void inject(MainActivity mainActivity);

    void inject(UserAuthFragment userAuthFragment);

    void inject(LectureHomePresenter lectureHomePresenter);

    void inject(LecturerViewClassPresenter lecturerViewClassPresenter);

    void inject(HomeFragmentStudent homeFragmentStudent);

    void inject(RatingFragmentLecturer ratingFragment);

    void inject(LectureRatingPresenter lectureRatingPresenter);

    void inject(StudentHomePresenter studentHomePresenter);

    void inject(StudentRatingPresenter studentRatingPresenter);

    void inject(StudentListingPresenter studentListingPresenter);
}
