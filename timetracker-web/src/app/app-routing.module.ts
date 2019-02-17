import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { HistoryComponent } from './history/history.component';
import { TimetrackComponent } from './timetrack/timetrack.component';

const routes: Routes = [
  { path: '', component: HistoryComponent },
  { path: 'timetrack', component: TimetrackComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
