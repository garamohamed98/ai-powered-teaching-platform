import {Component, Input} from '@angular/core';
import {Card} from 'primeng/card';
import {TableModule} from 'primeng/table';
import {Message} from 'primeng/message';
import {Skeleton} from 'primeng/skeleton';
import {Exercise} from '../../models/exercise.model';

@Component({
  selector: 'app-exercises-table',
  imports: [
    Card,
    TableModule,
    Message,
    Skeleton
  ],
  templateUrl: './exercises-table.component.html',
  styleUrl: './exercises-table.component.scss'
})
export class ExercisesTableComponent {

  @Input({required: true}) maxRows!: number;
  @Input({required: true}) exercises!: Exercise[];
  @Input({required: true}) loading!: boolean;
}
