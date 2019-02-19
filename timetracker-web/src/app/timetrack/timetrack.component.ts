import { Component, OnInit } from '@angular/core';
import { DataService } from '../data.service';

@Component({
  selector: 'app-timetrack',
  templateUrl: './timetrack.component.html',
  styleUrls: ['./timetrack.component.css']
})
export class TimetrackComponent implements OnInit {

  emailAddress:string = '';
  startDate:string = '';
  startTime:string = '';
  endDate:string = '';
  endTime:string = '';
  isLoading = false;

  constructor(public dataService:DataService) { }

  ngOnInit() {
  }

  create() {
    if(this.emailAddress == '' || this.startDate == '' || this.startTime == '' ||
        this.endDate == '' || this.endTime == '') {
      window.alert("Parameters cannot be empty!");
      return;
    }

    this.isLoading = true;

    let newTimeTrackRecord = {
      email: this.emailAddress,
      startDateTime : this.createDate(this.startDate, this.startTime),
      endDateTime : this.createDate(this.endDate, this.endTime)
    };

    console.log(newTimeTrackRecord);

    this.dataService.postTimeTrackRecord(newTimeTrackRecord).subscribe((result) => {
      if(result != undefined) {
        window.alert("Timetrack record was created!");
        this.clearFields();
      }

      this.isLoading = false;

    }, (err) => {
      console.log(err);
      window.alert("An error occured while creating a new record!");
      this.isLoading = false;
    });
  }

  private clearFields() {
    this.emailAddress = '';
    this.startDate = '';
    this.startTime = '';
    this.endDate = '';
    this.endTime = '';
  }

  private createDate(dateStr:string, timeStr:string) {
    let dateParts:any = dateStr.split('-');
    let timeParts:any = timeStr.split(':');
    let createdDate:any = null;

    if(dateParts && timeParts) {
        dateParts[1] -= 1;
        createdDate = new Date(Date.UTC.apply(undefined,dateParts.concat(timeParts))).toISOString();
    }

    return createdDate;
  }
}
