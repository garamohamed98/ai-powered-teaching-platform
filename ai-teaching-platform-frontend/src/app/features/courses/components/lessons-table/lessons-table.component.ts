import {Component, Input} from '@angular/core';
import {Lesson} from '../../models/lesson.model';
import {Card} from 'primeng/card';
import {TableModule} from 'primeng/table';
import {Skeleton} from 'primeng/skeleton';
import {Message} from 'primeng/message';

@Component({
  selector: 'app-lessons-table',
  imports: [
    Card,
    TableModule,
    Skeleton,
    Message
  ],
  templateUrl: './lessons-table.component.html',
  styleUrl: './lessons-table.component.scss'
})
export class LessonsTableComponent {

  @Input({required:true}) maxRows!: number;
  @Input({required:true}) lessons!: Lesson[];
  @Input({required:true}) loading!: boolean;

}
