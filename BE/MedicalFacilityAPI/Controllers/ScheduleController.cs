using MedicaiFacility.BusinessObject;
using MedicaiFacility.Service.IService;
using Microsoft.AspNetCore.Mvc;

// For more information on enabling Web API for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace MedicalFacilityAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class ScheduleController : ControllerBase
    {
        private readonly IMedicalExpertScheduleService _mediicalExpertScheduleService;
        private readonly IUserService _userService;
        public ScheduleController(IMedicalExpertScheduleService mediicalExpertScheduleService, IUserService userService)
        {
            _mediicalExpertScheduleService = mediicalExpertScheduleService; 
            _userService = userService;
        }
        // GET: api/<ScheduleController>
        [HttpGet]
        public ActionResult<IEnumerable<MedicalExpertSchedule>> GetAll([FromQuery]int expertId)
        {

            var item  = _mediicalExpertScheduleService.GetSchedulesByExpertId(expertId).Where(x=>x.IsActive==true);

           return Ok(item);
        }

        // GET api/<ScheduleController>/5
        [HttpGet("{scheduleid:int}")]
        public ActionResult<MedicalExpertSchedule> Get(int scheduleid)
        {
            var item = _mediicalExpertScheduleService.GetSchedulesByExpertId(0);
            var result = item.FirstOrDefault(x=>x.ScheduleId== scheduleid);
            if(result==null) return NotFound();
            return result;
        }

        // POST api/<ScheduleController>
        [HttpPost]

        public ActionResult<string> Post([FromBody] MedicalExpertScheduleRequest request)

        {
            var allUser = _userService.GetAllExpertMedical();
            if (allUser != null) {
                var check = allUser.Where(x => x.UserId == request.ExpertId);

                if (check == null) return BadRequest("not found expert id");

            }
            var item = new MedicalExpertSchedule {
                ExpertId = request.ExpertId,
                StartDate = request.StartDate,
                EndDate = request.EndDate,
                DayOfWeek = request.DayOfWeek,
                IsActive = true
            };

            var result = _mediicalExpertScheduleService.AddMedicalExpertSchedule(item);
            if(!result.Equals("Tạo thành công")) return BadRequest(result);
            return Ok(result);

        }

        // PUT api/<ScheduleController>/5
        [HttpPut("{medicalExpertScheduleId:int}")]
        public ActionResult<MedicalExpertSchedule> Put(int medicalExpertScheduleId, [FromBody] MedicalExpertScheduleRequest request)
        {
            var allUser = _userService.GetAllExpertMedical();
            if (allUser != null)
            {
                var check = allUser.Where(x => x.UserId == request.ExpertId);
                if (check == null) return BadRequest(new { message = "not found expert id" });
            }

            var item = _mediicalExpertScheduleService.GetSchedulesByExpertId(0);
            var result = item.FirstOrDefault(x => x.ScheduleId == medicalExpertScheduleId);
            if(result == null) return NotFound();
            result.ExpertId = request.ExpertId;
            result.DayOfWeek = request.DayOfWeek;
            result.StartDate = request.StartDate;
            result.EndDate = request.EndDate;
            result.IsActive = request.IsActive??true;
            return _mediicalExpertScheduleService.UpdateMedicalExpertSchedule(result);
        }
        [HttpPut("delete/{medicalExpertScheduleId:int}")]
        public ActionResult<MedicalExpertSchedule> Delete(int medicalExpertScheduleId)
        {
         

            var item = _mediicalExpertScheduleService.GetSchedulesByExpertId(0);
            var result = item.FirstOrDefault(x => x.ScheduleId == medicalExpertScheduleId);
            if (result == null) return NotFound();
           
            result.IsActive = false;
            return _mediicalExpertScheduleService.UpdateMedicalExpertSchedule(result);
        }

    }
    public class MedicalExpertScheduleRequest {
        public int? ExpertId { get; set; }

        public string DayOfWeek { get; set; }
        public DateTime StartDate { get; set; }

        public DateTime EndDate { get; set; }
        public bool? IsActive { get; set; }
    }
}
