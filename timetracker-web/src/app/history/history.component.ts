import { Component, OnInit } from '@angular/core';
import { DataService } from '../data.service';

@Component({
  selector: 'app-history',
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.css']
})
export class HistoryComponent implements OnInit {

  DEFAULT_LENGTH = 10;

  emailAddress:string = '';
  records:any = [];
  isLoading = false;
  hasMore = false;

  constructor(public dataService:DataService) { }

  ngOnInit() {
  }

  public search() {
    if(this.emailAddress == '') return;

    this.hasMore = false;
    this.records = [];
    this.getTimeTrackRecords(this.emailAddress, 0);
  }

  public loadMore() {
    if(this.records.length == 0) return;

    this.getTimeTrackRecords(this.records[0].email, this.records.length);
  }

  private getTimeTrackRecords(email:string, offset) {
    this.isLoading = true;

    this.dataService.getTimeTrackRecords(email, offset, this.DEFAULT_LENGTH).subscribe((data: []) => {
      console.log(data);

      if(data != undefined && data.length > 0) this.records = this.records.concat(data);

      this.hasMore = data != undefined && data.length == this.DEFAULT_LENGTH;

      this.isLoading = false;
    });
  }

  public hasRecords() {
    return this.records != null && this.records.length > 0;
  }
}
