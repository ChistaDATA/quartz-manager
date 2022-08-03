import { Component, OnInit } from '@angular/core';
import {
  ConfigService,
  UserService
} from '../../services';

@Component({
  selector: 'manager',
  templateUrl: './manager.component.html',
  styleUrls: ['./manager.component.scss']
})
export class ManagerComponent implements OnInit {

  newTriggerFormOpened = false;

  constructor(
    private config: ConfigService,
    private userService: UserService
  ) { }

  ngOnInit() {
  }

  setnewTriggerFormOpened(opened: boolean){
    this.newTriggerFormOpened = opened;
  }

}
