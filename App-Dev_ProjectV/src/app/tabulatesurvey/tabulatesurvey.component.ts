import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Question, Option } from './tabulate';
import { ActivatedRoute } from '@angular/router';
import { TabulateService } from './tabulate.service';
import { Chart, registerables } from 'node_modules/chart.js';
Chart.register(...registerables)

import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-tabulatesurvey',
  templateUrl: './tabulatesurvey.component.html',
  styleUrls: ['./tabulatesurvey.component.css']
})
export class TabulatesurveyComponent implements OnInit {
  questions!: Question[];
  actualQuestion: Question = new Question;
  codeSurvey!: string | null;
  text!: string;
  nChart: Chart | undefined;
  StudentArray : any[] = [];
  isResultLoaded = false;
  isUpdateFormActive = false;
  constructor(private tabulateService: TabulateService, private route: ActivatedRoute, private cdr: ChangeDetectorRef, private http: HttpClient) 
  {
    this.getAllStudent();
   }

  ngOnInit(): void {
    this.codeSurvey = this.route.snapshot.paramMap.get('codeSurvey');
    this.listQuestion();
  }

  public listQuestion() {
    this.tabulateService.getQuestions(this.codeSurvey).subscribe(
      (data) => {
        this.questions = data;
        this.actualQuestion = this.questions?.[0];
        this.tabulateData();
      }
    );
  }
  getAllStudent()
  { 
    this.http.get("http://localhost:7000/api/student")
    .subscribe((resultData: any)=>
    {
        this.isResultLoaded = true;
        console.log(resultData.data);
        this.StudentArray = resultData.data;
        
    });
  }
  setDelete(data: any) {
    if (confirm("Are you sure you want to delete this student?")) {
      this.http.delete("http://localhost:7000/api/student/delete/" + data.id)
        .subscribe((resultData: any) => {
          console.log(resultData);
          alert("Student Deleted");
          this.getAllStudent(); // Refresh student list
        }, error => {
          console.error('Error deleting student:', error);
          alert("Error deleting student. Please try again.");
        });
    }
  }
  

  tabulateData() {
    let options: Option[] = this.actualQuestion.options;

    if (this.nChart) {
      this.nChart.destroy();
    }
    
    this.nChart = new Chart(('graphic'), {
      type: 'bar',
      data: {
        labels: options.map(row => row.description),
        datasets: [
          {
            label: 'Total responses',
            data: options.map(row => row.count),
            backgroundColor: 'rgba(0, 112, 255, 0.5)'
          }
        ]
      }
    });
  }
}


