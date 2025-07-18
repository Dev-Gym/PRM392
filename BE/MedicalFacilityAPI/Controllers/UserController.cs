using MedicaiFacility.BusinessObject;
using MedicaiFacility.DataAccess.IRepostory;
using MedicaiFacility.Service.IService;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace MedicalFacilityAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class UserController : ControllerBase
    {
        private readonly IUserService _service;
        public UserController(IUserService service)
        {
            _service = service;
        }

        [HttpGet("get-all-experts")]
        public ActionResult<List<User>> GetAllExperts() {
            var item = _service.GetAllExpertMedical();
            return Ok(item);
        }

        [HttpGet("get-all-patient")]
        public ActionResult<List<User>> GetAllPatients()
        {
            var item = _service.GetAllPatient();
            return Ok(item);
        }

    }
}
